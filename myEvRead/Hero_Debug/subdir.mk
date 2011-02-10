################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../main.c 

OBJS += \
./main.o 

C_DEPS += \
./main.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	/home/yigiter/android/hero/prebuilt/linux-x86/toolchain/arm-eabi-4.4.0/bin/arm-eabi-gcc -DANDROID -I/home/yigiter/android/hero/system/core/include -I/home/yigiter/android/hero/hardware/libhardware/include -I/home/yigiter/android/hero/hardware/libhardware_legacy/include/hardware_legacy -I/home/yigiter/android/hero/hardware/ril/include -I/home/yigiter/android/hero/dalvik/libnativehelper/include -I/home/yigiter/android/hero/frameworks/base/include -I/home/yigiter/android/hero/frameworks/base/opengl/include -I/home/yigiter/android/hero/external/skia/include -I/home/yigiter/android/hero/out/target/product/hero/obj/include -I/home/yigiter/android/hero/bionic/libc/arch-arm/include -I/home/yigiter/android/hero/bionic/libc/include -I/home/yigiter/android/hero/bionic/libstdc++/include -I/home/yigiter/android/hero/bionic/libc/kernel/common -I/home/yigiter/android/hero/bionic/libc/kernel/arch-arm -I/home/yigiter/android/hero/bionic/libm/include -I/home/yigiter/android/hero/bionic/libthread_db/include -I/home/yigiter/android/hero/bionic/libm/arm -I/home/yigiter/android/hero/bionic/libm -I/home/yigiter/android/hero/out/target/product/hero/obj/SHARED_LIBRARIES/libm_intermediates -O0 -g3 -Wall -c -fmessage-length=0 -msoft-float -fpic -ffunction-sections  -funwind-tables -fstack-protector -fno-short-enums -fno-exceptions -Wno-multichar -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


