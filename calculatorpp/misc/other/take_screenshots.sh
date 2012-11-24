#!/bin/bash

declare -a skins=("WVGA854" "WQVGA400" "HVGA" "WQVGA432" "WVGA800" "QVGA")
declare -a targets=("android-16")

for target in ${targets[@]}
do
    for skin in ${skins[@]}
    do
        avdDeviceName=AVD_$skin
        $ANDROID_HOME/tools/emulator -avd $avdDeviceName &
        $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/aux/tmp $avdDeviceName
        $ANDROID_HOME/platform-tools/adb -s emulator-5554 emu kill
    done
done