# For some reason, ant sometimes needs the CLASSPATH set and won't just use what is in ~/.ant/lib

default: build_rpm

dist/lib/MetlogHive.jar:
	CLASSPATH=~/.ant/lib/ivy-2.3.0-rc1.jar ant

build_rpm: 	dist/lib/MetlogHive.jar
	mkdir -p build/SOURCES
	rm -rf build/RPMS build/metlog-hive-*
	tar czf build/SOURCES/metlog-hive.tar.gz dist/lib
	rpmbuild --define "_topdir $$PWD/build" -ba rpm/metlog-hive.spec
	cp build/RPMS/*/*.rpm build/
	ls -l build/metlog-hive*.rpm

clean:
	rm -rf build dist
