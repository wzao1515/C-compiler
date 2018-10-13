.386
.model flat,stdcall
option casemap:none
.data
k	dword	?	
j	dword	?	
i	dword	?	
.code
start:
	mov	i,eax	
L1:
	mov	eax,i	
	mov	ebx,10	
	cmp	eax,ebx	
	jnb	L2	
	mov	eax,i	
	mov	ebx,5	
	cmp	eax,ebx	
	jna	L3	
	inc	i	
L3:
	inc	i	
	jmp	L1	
L2:
	ret
end start