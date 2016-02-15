for i in $(seq 1 5); do
	cd /root/apache-tomcat-6.0.20-$i/bin
	./catalina.sh start
	sleep 2
done
for i in $(seq 1 5); do
	cd /root/apache-tomcat-6.0.20-$i-b/bin
	./catalina.sh start
	sleep 2
done
