![Build Status](https://github.com/the-atari-team/wnf.compiler/actions/workflows/maven.yml/badge.svg)

# Atari 8bit WNF Compiler

Yes! The first ideas for such compiler are over 30 years old!

de_DE:: WNF für "WiNiFe"-Projekt oder "Wird nie fertig" (ak. Baumann und Clausen Comedy)

en_EN:: WNF stays for "WiNeFi" project or "Will never finished" (german gov. comedy)

## What is this?

_This is a hobby project._ It is a compiler for an Algol 68 like language.
It only produces assembler source code for the old [MOS-6502](https://en.wikipedia.org/wiki/MOS_Technology_6502) CPU.
The compiler based also on ideas of [Jack Crenshaws](https://compilers.iecc.com/crenshaw/) documents "Let's Build a Compiler".
It is hardcoded to 6502 assembler for simplicity.
The assembler files are full compatible to
[atasm-Assembler](https://github.com/CycoPH/atasm/),
and mostly compatible to mac/65 from OSS.

## For what?

This is just a simple but full working demonstration to build 6502 assembler code for computers like Atari 8bit.

## Why another compiler

This is also a demonstration for myself.
I would like to create a language something faster than Turbo-Basic XL.
A simple language which also supports assembler functions in a simple manner.
Something I can also use in Turbo-Basic.

* Action! has the limitation of the need of an expensive cartridge. (350,-DM in 1984) Also programs which use the runtime need the cartridge to run. Nevertheless was Action! a wunderful high speed language at this time.
* Atmas-II Assembler has a very nice editor but much memory limitations and do not support includes. So it was just an assembler for very small projects.
* Mac65 was a very nice assembler to build real fat projects, but the editor is a pain.
* cc65 is a monster, it uses C syntax with boiler plate ';' etc.
* kickc use also C syntax

### Features

The compiler supports:

#### Language
* Simple language like Algol 68 with very few reserved word.
* Single pass compiler, no prototypes. But call of _unknown_ functions if functionname starts with @ is supported.

* Complex expressions also with call to self created functions.
* 2-complement mult and div can be done by shift
* A star chain feature to convert fix value _multiplications_ to shift/add/sub memonics.
* Full access to the mult/div code so a replacement to a faster version if only 8 bit is need to `a*b = ((a+b)^2)/4 - ((a-b)^2)/4` is possible.
* Numbers can give in 10base(default), 16base(hex), 2base(binary) and 4base(quad helpful for 2bit graphics)
* Variable types are byte(unsigned), word(signed), int8(signed) or uint16(unsigned) also in arrays
* Variables can set to fix addresses in memory
* There exist no pointers but an address access feature to give access to its address. So a function call with a string as parameter like `@printf("Hello World")` is possible.

* One-dimensional arrays. It takes care of the index access on the 8bit machine. So arrays smaller than 256 bytes are simple accessed by index access. Word wide arrays with lower than 256 elements will splitt in low and high byte arrays for fast index access.
* Strings with length up to 255 bytes are something like byte arrays.
* Arrays of strings are possible.

* Supports only word wide (2byte) parameter but in any amount. Parameter access is very simple also in self written assembler code. We use an extra heap pointer for the parameter access and not the small 6502 Stack.
* Parameters in functions/procedures will give by value. If the value is an address, handle it as an address. There are no checks.
* Only global variables, due to the fact of 64KiB limitation. But in functions/procedures already generated variables can make local. These variables will backup one the heap.

* Recursion up to ~120 deep calls also with many parameters. Parameters will store on extra heap and not on the small stack.
* Full control over the source. No cartridge need.
* There exist a simple peephole optimizer.
* The compiler produce readable assembler source code with extra comments of the corresponding source code. Helpful for debugging or check the code.

#### Hardware Features
* Gives near full control over the Atari 8bit machine.
* DLI/Interrupt sub routines could write in this language, but it is not recommended.
* The code has a relative little footprint in the zero page. It uses only the floating point registers of the Atari (212-255) so it works best together with Atari Basic. Also supports simple parameter access from Basic.

* The default runtime is very small. Other code is stored in include files and can import if need. No automatism for such external code is implemented.

* Memory limit on Atari: you can create single files from $1000 up to $bfff loadable by DOS.

### Not supported

* Pointer arithmetic like in C is not supported, use array access instead.
* The tristate operator <condition> ? <true> : <false> do not exist.
* Pre/Postincrement `++V V++ --V V--` are not supported use of `V:=V+1` will result in fast `INC V` assembler code. `V[i] := V[i] + 1` will result in `LDX I; INC V,X`.
* Range control in array access is not supported.
* Floating Point Arithmetic
* Datatypes/Structs
* Dynamic Memory allocation
* Switch case statements
* Inline functions
* Inline assembler, but include to load foreign code.
* Conditions are not mixable like `(a and b) or c` is not possible.
* do loop endless loop not exists. Use `while 1=1 do <statement>` instead.
* Good dynamic optimizer do not exist, just a simple peephole optimizer.

### Other limitations
* The biggest limitation, you need Java to run the compiler. ;-) There exists only a very old version outdated native version of the compiler in turbo basic with much less features.
* There exists only the header files for the Atari 8bit.
* It is hardcoded for the [MOS-6502](https://en.wikipedia.org/wiki/MOS_Technology_6502) CPU.
* It produces only assembler source code. You need the [atasm-Assembler](https://github.com/CycoPH/atasm/) to create Atari binary files.
* No linker only the possibility to check which include has to add by hand.
* No debugger, only the source code lines will be in stored in the assembler source code with line numbers. With modern atasm assembler version 1.17 and Altirra you can debug the assembler source where the compiler sources build in. Very helpful, but not perfect.


## Licenses
The Atari 8bit WNF Compiler sources are licensed under
[Creative Commons Licenses](https://creativecommons.org/licenses/by-sa/2.5/).


This small TurboBasic application has been rewritten in Java language
since october 2020.

It reads Atari 8bit wnf-files and compile these files to 6502-assembler.
The assembler files are full compatible to
[atasm-Assembler](https://github.com/CycoPH/atasm/),
and mostly compatible to mac/65 from OSS.

* [x] read full wnf file
* [x] expressions
* [x] generate assembler of expressions
* [x] peephole optimizer


## TODOs
* [ ] lot of german comments, sorry

## Build
This is a Apache Maven based Java program. You need at least OpenJDK 8.
To build it, just call `mvn verify` or use the
bash-script `./build.sh`, which also simply call Apache-Maven.

There exist a lot JUnit tests, therefore you see lot of output.
The source-code-coverage of tests is more then 95%.


## Installation
After successful build, copy the `compiler/target/wnf-compiler.jar` file
to a directory, where a path in your $PATH variable shows to.
Then create the following simple script like
[source]
#!/bin/bash
SCRIPT=$(dirname $0)
java -jar ${SCRIPT}/wnf-compiler.jar $@

Rename this script to `wnfc` and give it execute flags like `chmod +x wnfc`.
Now on console, call `wnfc` and you will get the usage information of the compiler.

Here also a script exists.
`./deploy.sh` but due to the fact that every user has a different environment, you should not use it.
It is just for me to install this compiler on my Windows (mingw) PC
and on my Linux PC. Maybe you find it useful.


## Parameter
Usage::
`wnfc [OPTIONS] [FILE]`

**OPTIONS**

-O level
: Give the wished optimization level,

* 0 for no optimization,
* 1 for normal optimization,
* 2 for also branch optimizations. +
The wnfc compiler contains a very rudimentare peephole optimizer.
Which takes a look into the generated assembler source and replace some constructs by faster one. +
The compiler produce really simple code with parameter -O 0.

-v level
: Give the verbose level

-I path
: Path where to search for include files.
The current path is default. +
The -I parameter can give more than once.

-o path
: The output path, where to copy the generated ASM/INC files.

-smc
: If given, allow self modified code (smc). +
This is very experimental, do not use!

-noscm
: Deactivate the shift/(add|sub) for hard coded multiplications.

-noshift
: Multiplication/divisions with 2-complement will not use. e.g. x := y * 2 or x:=y / 4...

-smallHeapPtr
: if given, only 256 bytes will use as heap. This means less than 256 Bytes for parameter and local variables. Be careful!

-noHeader
: If given, header.wnf will not automatically import, if file exists.

-showVariableUsage
: Show how often each variable is used.

-showVariableUnused
: Show if variable is used in code.

-showPeepholeOptimize
: Show which optimization is applied.

-precalculate (w|e)
: If given, expressions without variables may be precalculatable. This will result in warnings or build errors. If such expression found.

- testincludes
: If given, a check for all functions will be done. If an include is missed, it will produce a warning.

- boundscheck or -bc
: Add bounds check code. An access outside the array will panic the code.

-noSaveLocalToStack
: If given local variables will store on heap. The new default is to store local variables on the small 6502 stack.

-verbose level
: Set the verbose level, default is 2.

-h
: Show this help.

FILE
: Give one source file. Currently, it is not possible to
give more than one file.


## PLAIN

This is only the plain compiler to compile wnf-files to 6502-assembler.
Currently, you can only compile the Oxygene Be sources.

More include files to build other things will follow soon.

## Sorry
This Java Application has been created very fast out of my original
compiler written in Turbo-Basic-XL.
See link:[turbobasic](turbobasic/README.adoc).
Due to the fact it is a very old german project (over 30 years old), lots of comments
also in the Java source are in german.

You need to name this compiler `wnfc` to build Oxygene Be from source, because there exists a Makefile in the sources which use wnfc as compiler.

## Hello World example

Create a file `hello-world.wnf` with content:
```
 program hello
 lomem=$4000,runad
 begin
   @printf('Hello World!\n')
 end
 include 'SCREEN_OUTPUT.INC'
 include 'PRINTF.INC'
```

To build the assembler code we need to know where to find the include files. Here we assume in variable $COMPILER_LIB

`wnfc hello-world.wnf -I $(COMPILER_LIB)`

This will produce an assembler program HELLO.ASM which we need to assemble.

`atasm HELLO.ASM`

Will assemble the `HELLO.ASM` to an executable `HELLO.65o`.

`atari800 HELLO.65o`

Starts an atari emulator and loads the HELLO.65o into memory. Due to the `lomem=$4000,runad` the compiler adds the start feature of Atari files so such files can load by DOS with autostart.

The executable here `*.65o` is equivalent to `*.COM` files.

I recommend the use of Makefiles to build such programs.
