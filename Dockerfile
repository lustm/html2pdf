FROM openjdk:11.0.12-jre-buster

RUN sed -i s@/deb.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN sed -i s@/security.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN apt-get clean && apt-get update
RUN apt-get install wkhtmltopdf -y
RUN apt-get install -y locales
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENV LANG zh_CN.utf8
ENV  TIME_ZONE Asiz/Shanghai

VOLUME /tmp

ADD ./target/html2pdf-0.0.1-SNAPSHOT.jar /html2pdf-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-XX:+UnlockExperimentalVMOptions","-XX:MaxRAMPercentage=80.0","-Duser.timezone=GMT+08","-XX:+HeapDumpOnOutOfMemoryError","-Djava.security.egd=file:/dev/./urandom","-jar","/html2pdf-0.0.1-SNAPSHOT.jar"]
