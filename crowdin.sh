#!/bin/bash

rm -r translations
unzip calculatorpp.zip -d translations

function copyTranslation {
    from=$1
    to=$2

	if [ ! -d $to ]; then
    	# if directory doesn't exist create it
    	mkdir $to
    fi

    cp $from $to
}

copyTranslation "translations/en/app-android/*" "android-app-core/res/values"
copyTranslation "translations/es-ES/app-android/*" "android-app-core/res/values-es"
copyTranslation "translations/de/app-android/*" "android-app-core/res/values-de"
copyTranslation "translations/fr/app-android/*" "android-app-core/res/values-fr"
copyTranslation "translations/it/app-android/*" "android-app-core/res/values-it"
copyTranslation "translations/nl/app-android/*" "android-app-core/res/values-nl"
copyTranslation "translations/pl/app-android/*" "android-app-core/res/values-pl"
copyTranslation "translations/pt-BR/app-android/*" "android-app-core/res/values-pt-rbr"
copyTranslation "translations/ru/app-android/*" "android-app-core/res/values-ru"
copyTranslation "translations/uk/app-android/*" "android-app-core/res/values-uk"
copyTranslation "translations/zh-CN/app-android/*" "android-app-core/res/values-zh-rtw"


rm -r translations
