#!/bin/bash

rm -r translations
unzip calculatorpp.zip -d translations

function copyTranslation() {
    from=$1
    to=$2

    cp ${from} ${to}
}

function copyTranslations() {
    from="translations/$1/app-android/*"
    to="android-app/src/main/res/values-$2"
    if [ -z "${2}" ]; then
        to="android-app/src/main/res/values"
	fi

	if [ ! -d ${to} ]; then
    	# if directory doesn't exist create it
    	mkdir ${to}
    fi

	copyTranslation "${from}" "${to}"

	from="translations/$1/app/messages.properties"
    to="core/src/main/resources/org/solovyev/android/calculator/messages_${1//[-]/_}.properties"
    if [ -z "${2}" ]; then
        to="core/src/main/resources/org/solovyev/android/calculator/messages.properties"
	fi

	copyTranslation "${from}" "${to}"

	from="translations/$1/jscl/messages.properties"
    to="../../jscl/src/main/resources/jscl/text/msg/messages_${1//[-]/_}.properties"
    if [ -z "${2}" ]; then
        to="../../jscl/src/main/resources/jscl/text/msg/messages.properties"
	fi

	copyTranslation "${from}" "${to}"
}

copyTranslations "ar" "ar"
copyTranslations "cs" "cs"
copyTranslations "en" ""
copyTranslations "es-ES" "es"
copyTranslations "de" "de"
copyTranslations "fi" "fi"
copyTranslations "fr" "fr"
copyTranslations "it" "it"
copyTranslations "nl" "nl"
copyTranslations "pl" "pl"
copyTranslations "pt-BR" "pt-rbr"
copyTranslations "pt-PT" "pt-rpt"
copyTranslations "ru" "ru"
copyTranslations "vi" "vi"
copyTranslations "uk" "uk"
copyTranslations "ja" "ja"
copyTranslations "zh-CN" "zh-rcn"
copyTranslations "zh-TW" "zh-rtw"


rm -r translations
