#!/bin/bash


declare -a skins=("WVGA854" "WQVGA400" "HVGA" "WQVGA432" "WVGA800" "QVGA")
declare -a targets=("android-16")

for target in ${targets[@]}
do
    for skin in ${skins[@]}
    do
        $ANDROID_HOME/tools/android -s delete avd -n AVD_$skin
    done
done


