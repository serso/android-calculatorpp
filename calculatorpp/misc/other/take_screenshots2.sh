#!/bin/bash

# "AVD_4.1_x86_6" "AVD_4.0.3_x86" "AVD_4.1_x86" "Default" "AVD_4.0.3_x86_7" "AVD_4.1_x86_7" "AVD_4.1_x86_9.5" "Galaxy_Tab" "AVD_4.1_x86_4"
# "AVD_4.1_x86_6" "AVD_4.1_x86_7" "AVD_4.1_x86_9.5" "AVD_4.1_x86_4"

declare -a names=("AVD_4.1_x86_4")

for name in ${names[@]}
do
    $ANDROID_HOME/tools/emulator -avd $name &
    $ANDROID_HOME/tools/monkeyrunner ./take_screenshots.py ~/projects/java/android/calculatorpp/calculatorpp/misc/aux/tmp $name
    $ANDROID_HOME/platform-tools/adb -s emulator-5580 emu kill
done