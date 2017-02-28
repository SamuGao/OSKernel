  BOOTSEG = 07C00H
  SYSSEG  = 01000H
  SYSLEN  = 17
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
  jmp 01000h:0000h
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
  mov ax, cs
  mov ds, ax
  mov es, ax
  call ShowHello
  jmp $
ShowHello:
  mov ax, HelloMessage
  mov bp, ax
  mov cx, 18
  mov ax, 01301h    
  mov bx, 000ch
  mov dx, 0000h
  int 10h
  ret
  HelloMessage db "Welcome to SemiOS!"