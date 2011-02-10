################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: HTC

# Tool invocations
HTC: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	/home/yigiter/android/hero/prebuilt/linux-x86/toolchain/arm-eabi-4.4.0/bin/arm-eabi-gcc -nostdlib -Bdynamic  -Wl,-T,/home/yigiter/android/hero/build/core/armelf.x -Wl,-dynamic-linker,/system/bin/linker -Wl,--gc-sections -Wl,-z,nocopyreloc -Wl,--no-undefined -Wl,-rpath-link=/home/yigiter/android/hero/out/target/product/hero/obj/lib -Wl,-rpath,/system/lib -L/home/yigiter/android/hero/out/target/product/hero/obj/lib  /home/yigiter/android/hero/out/target/product/hero/obj/lib/crtbegin_dynamic.o /home/yigiter/android/hero/prebuilt/linux-x86/toolchain/arm-eabi-4.4.0/lib/gcc/arm-eabi/4.4.0/libgcc.a /home/yigiter/android/hero/out/target/product/hero/obj/lib/crtend_android.o -lc -lm -o"HTC" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(C_DEPS)$(EXECUTABLES) HTC
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
