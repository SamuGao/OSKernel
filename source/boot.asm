include 'Structures.inc'
include 'Constant.inc'
org BOOTSEG
  use16
  mov ax, BOOTSEG
  mov ds, ax
  mov ss, ax
  mov sp, 0400h

LoadSystem:
  mov dx, 0000h ;磁头[0,1]，驱动器号[0x0~0x7f软盘,0x80~0xff硬盘]
  mov cx, 0002h ;柱面[0~79],扇区[1~18]
  mov ax, SYSSEG
  mov es, ax
  xor bx, bx
  mov ax, 0200h+SYSLEN
  int 13h

  jc ErrInfo
SysStart:
  jmp SYSSEG:0000h
ErrInfo:
  mov ax, cs
  mov ds, ax
  mov es, ax
  call DispStr
  jmp $
DispStr:
  mov ax, BootMessage
  mov bp, ax
  mov cx, 21
  mov ax, 01301h
  mov bx, 000ch
  mov dl, 0
  int 10h
  ret

BootMessage db "Read soft disk error!"
times 510-($-$$) db 0
dw 0xaa55

org 0h
	jmp	LABEL_BEGIN ;跳转到开始处执行

; 全局描述符定义
LABEL_GDT   	   Descriptor 0, 0, 0, 0, 0  ; 空描述符
LABEL_DESC_CODE32  Descriptor 0, 0, 0, 0, 0  ; 非一致代码段
LABEL_DESC_VIDEO   Descriptor 0, 0, 0, 0, 0  ; 显存首地址

GdtLen		equ	$ - LABEL_GDT	; 全局描述符长度
GdtPtr		dw	GdtLen - 1	; 全局描述符界限
		      dd	0		; GDT基地址

; 描述符选择子
SelectorCode32		equ	LABEL_DESC_CODE32	- LABEL_GDT
SelectorVideo		equ	LABEL_DESC_VIDEO	- LABEL_GDT

LABEL_BEGIN:
  use16
	mov	ax, cs
	mov	ds, ax
	mov	es, ax
	mov	ss, ax
	mov	sp, 0100h

	; 初始化 32 位代码段描述符
	mov word [LABEL_DESC_CODE32], SegCode32Len - 1 ; 段界限0~15
	xor	eax, eax
	mov	ax, cs
	shl	eax, 4
	add	eax, LABEL_SEG_CODE32
	mov	word [LABEL_DESC_CODE32 + 2], ax ;段基址0~15
	shr	eax, 16
	mov	byte [LABEL_DESC_CODE32 + 4], al ;段基址16~23
	mov	byte [LABEL_DESC_CODE32 + 7], ah ;段基址24~31
	mov eax, SegCode32Len - 1
	shr eax, 8
	and ax, 0F00h
	mov bx, DA_C + DA_32
	or ax, bx
	mov word [LABEL_DESC_CODE32 + 5], ax

	; 初始化视频数据段段描述符
	mov word [LABEL_DESC_VIDEO], 0ffffh ; 段界限0~15
	xor	eax, eax
	mov	eax, 0B8000h
	mov	word [LABEL_DESC_VIDEO + 2], ax ;段基址0~15
	shr	eax, 16
	mov	byte [LABEL_DESC_VIDEO + 4], al ;段基址16~23
	mov	byte [LABEL_DESC_VIDEO + 7], ah ;段基址24~31
	mov eax, 0ffffh
	shr eax, 8
	and ax, 0F00h
	mov bx, DA_DRW
	or ax, bx
	mov word [LABEL_DESC_VIDEO + 5], ax

	; 为加载 GDTR 作准备
	xor	eax, eax
	mov	ax, ds
	shl	eax, 4
	add	eax, LABEL_GDT		; eax <- gdt 基地址
	mov	dword [GdtPtr + 2], eax	; [GdtPtr + 2] <- gdt 基地址

	; 加载 GDTR
	lgdt fword [GdtPtr]

	; 关中断
	cli

	; 打开地址线A20
	in	al, 92h
	or	al, 00000010b
	out	92h, al

	; 准备切换到保护模式
	mov	eax, cr0
	or	eax, 1
	mov	cr0, eax

	; 真正进入保护模式
	jmp	dword SelectorCode32:0	; 执行这一句会把 SelectorCode32 装入 cs,
					; 并跳转到 Code32Selector:0  处
; END of [SECTION .s16]


use32

LABEL_SEG_CODE32:
	mov	ax, SelectorVideo
	mov	gs, ax			; 视频段选择子(目的)

  cld
  mov si, WelcomeMessage
  mov ebx, (80 * 0 + 0) * 2 ;屏幕第 0 行, 第 0 列
  mov cx, 18
SHOW_WELCOME:
	mov	edi, ebx
	mov	ah, 0Ch			; 0000: 黑底    1100: 红字
	lodsb
	mov	[gs:edi], ax
  add ebx, 2
  loop SHOW_WELCOME

	; 到此停止
	jmp	$
WelcomeMessage db 'Welcome to SemiOS!'
SegCode32Len	=	$ - LABEL_SEG_CODE32