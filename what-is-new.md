# Atari 8bit WNF Compiler
:lang: en

## What is new
* Fix a problem with for loop `for i:=0 to n-1 do ...` where i is a word variable and n is a byte variable. `n-1` would compiled wrong. (Peephole problem fixed)

* Fix a problem with load random twice

* New function call aspect for "High Speed Parameters".
If a function/procedure name starts with @HSP_ it will use "High Speed Parameters" in the call.
The parameters will store in extra global variable area starts with @HSP_PARAM.
The first parameter will store at @HSP_PARAM+1(low byte) and @HSP_PARAM+2(high byte) and so on. Always 2 bytes will be used at function call creation.
Restrictions:
** Only self written Assembler functions (include at the end) can call with this "High Speed Parameters" feature.
** Minimum: At least one parameter must exist.
** Maximum: Up to 8 parameters are allowed. They should be in the zero page.
** @HSP_PARAM is defined as @REG+10, it uses the @REG Registers, so at first copy the values away.

* local variables will store on the 6502 stack per default,
  it saves some bytes and is faster instead of heap usage.
  but be careful, there are only 256 bytes in stack
  and every deeper JSR cost 2 bytes extra. There is no check at
  overrun.
  With new parameter -noSaveLocalToStack it will be possible to
  switch off store local variables on stack.

* Generate const variables as positions within an array
  byte array var[10] = [0,1,2,3,4,pos:here 5,6,7,8,9]
  so the const variable 'HERE' will have the value 5

* Parameter -boundscheck or -bc add some code to check the array bounds so a panic occur if write outside an array.

* Array access optimizations we support faster access where we only set the high byte of address in reserved zero page registers for read and write. The low byte in this registers will be always 0. The low byte is handled by y register so `load/store (address),y` are faster to realize.

* Array add like `a[i] := a[i] + 1` will optimize to `ldx i; inc a,x`
* Array sub like `a[i] := a[i] - 1` will optimize to `ldx i; dec a,x`

* test-sieve.wnf runs ~2s faster than Action! code

* Long Jump Optimizations, we can use JNE, JEQ, JCC, JCS, ... and optimize if most the time
  makes the code smaller and little bit faster

* neg:var to negate the given variable. Only with variables possible, faster than the function call

* condition check without condition memonic 'if a then ...' then will called if a != 0

* PUTARRAY Optimization, we use (@PUTARRAY0),y access, where the first value in memory must be
  always 0. To access the right byte position we use the y-Register. This saves some cycles at write.

* Strings are envelope by double quotes '"' Strings with a single value are possible also like "A".
* Single Characters are envelop by single quote. A given 'A' will be replaced by value 65.

* Due to a write problem to an array, we now support readonly values.
  It is not like const.
  `byte readonly a` now a can not be written. Helpful for some arrays.

* Assign multi values to the @mem array.
  `@mem[addr] := [1,2,3]` set addr=1, addr+1=2, addr+2=3 This only works with @mem array at the moment

* Due to the fact the compiler has no 'inline' for functions, the often need @abs(variable) is
  usable as abs:variable construct which produce faster but slightly more code.

* Parameter -testincludes if given, check if all used functions which start with @ are present
  in the given includes at the end of the program.
  If this is not the case, a WARNING will shown. It will try to find the need file to include.
  Due to the fact of the possibility of ambigous you must copy the include by hand.
  If a function can't found in all possible includes, not just the already included it will
  present also a WARNING.
  After compile, the needed assembler run will produce an error, if a function is not present.

* Parameter -precalculate (w|e) if given, every expression will check if the developer should
  calculate the fixed digits. The single path compiler can't help here? Maybe yes!

* `@mem[address] := <byte value>` this is a fast replacement for function call `@poke(address, <byte value>)`

* `value := @mem[address]` this is a fast replacement for function call `value := @peek(address)`

* We use StarChain in fix value multiplication like x*3. Then it will use shift/(add|sub).
  It is active by default. Remember multiply only works with positiv values.
  Also the fix value must be the last in a operation. 3*x will use @IMULT function.
+
The StarChain can completely deactivated by parameter `-noscm`.
The optimize for power of 2 is deactivated if parameter `-noshift` is given.

* Small Heap-Pointer with parameter `-smallHeapPtr`,
if given the heap is only 256 bytes big.
Could be too small for recursive function calls.
But saves 8 bytes per function/procedure call and up to 14 cycles per function/procedure call.

* `const variable=value` is a first try to support const values.
To get firm with the lot of magic values in code.

* First support of signed int (int8) -128 to 127
  in `for` loops, in normal conditions (`if`, `while`, `until`, `assert`)
  But be warned, there is no error if you overflow or underflow.
  In Expressions there will be a "autobox" conversion to word only.
+
`for i:=-5 to 5 do ...` are only possible if variable `i` is of type `int8`.

* Self-Modified-Code (smc) in for loop, activateable by parameter `-smc`, very experimental.

* Simplify the `for` loop. There is only one test in the loop.
This is much simpler but with a byte variable we can only count from 0 to 254.
With a word variable we can count from -32768 to 32766.
The last value is not arrivable any longer. But the code is faster/more clean.

* Update in `for` loop, `STEP` can set like in Basic.
+
`for i:=0 to 10 step 2 do ...`

* Function parameter name expansion like in C++ language.
Every function parameter is marked by an extra 'i' so a function name must contain also it's count of parameters.
If you have an external assembler function named by a name
  @name
  you need additionally give it's name with count of parameters, like
  @name_ii
  much better for error search, if you use one parameter too less or much.

One extra point:: Some functions allows different count of parameters, like @printf() function.
Now we need to add the parameters expansion here by give lot of extra i
+
  @printf_i
  @printf_ii
  @printf_iii
  @printf_iiii
  @printf_iiiii
  ...

looks stupid, but at the moment there is no better way. We do not support ... for variable args.

* Split word array access for arrays smaller than 257 items into a low byte and a high byte array.
So we can access with simple x/y-register access to `<array name>_low` and `<array name>_high` values,
which allows much faster access to it's values.
+
`word array name[@split] = address_name`
Assign an address to a word array and use it as split-array. In the assembler program you need to define all names right. Here:
+
```
address_name
address_name_low .byte <1,<2,0,...  ; sample values
address_name_high .byte >1,>2,0,...
```

* Due to the fact our program can exist of lot of files, all need to include, there exist one implicit header file. This header.wnf file can contain lot of const declarations you would like to use in different include files, also byte and word declarations are allowed. Everything what do not generate real code is possible.

