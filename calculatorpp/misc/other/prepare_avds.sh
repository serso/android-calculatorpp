#!/bin/bash


declare -a skins=("WVGA854" "WQVGA400" "HVGA" "WQVGA432" "WVGA800" "QVGA")
declare -a targets=("android-16")

for target in ${targets[@]}
do
    for skin in ${skins[@]}
    do
        $ANDROID_HOME/tools/android -s create avd -n AVD_$skin -t $target -b x86 -s $skin --force
    done
done

for target in ${targets[@]}
do
    for skin in ${skins[@]}
    do
        avdDeviceName=AVD_$skin
        $ANDROID_HOME/tools/emulator -avd $avdDeviceName &
        $ANDROID_HOME/tools/monkeyrunner ./wait_device.py
        $ANDROID_HOME/platform-tools/adb -s emulator-5554 emu kill
    done
done


