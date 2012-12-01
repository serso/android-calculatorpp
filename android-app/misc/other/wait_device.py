from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

print 'Waiting for device...'
device = MonkeyRunner.waitForConnection(100, 'emulator-5580')
print 'Finished'
if device :
    print 'Success'
else :
    print 'Failure'

