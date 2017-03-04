;------------------------结构体----------------------------------
;base0_15 : 段基址0~15位
;base16_23 : 段基址16~23位
;base24_31 : 段基址24~31位
;limit0_15 : 段界限0~15位
;attri : 属性（包括段界限16~19位）
struc Descriptor base0_15, base16_23, base24_31, limit0_15, attri
{
	.limit       dw	limit0_15	   ; 段界限(0~15)
	.basel       dw	base0_15	   ; 段基址1(0~15)
	.basem       db	base16_23      ; 段基址2(16~23)
	.attributes  dw	attri	       ; 属性（包含段界限16~19）
	.baseh       db	base24_31	   ; 段基址3(24~31)
}
