FROM openjdk:11

#현재 디렉토리의 build/libs/*.jar 경로에 있는 모든 JAR 파일을 새로운 이미지 내부의 appBatch.jar 파일로 복사합니다. 
#이는 프로젝트의 빌드된 Java 어플리케이션을 Docker 이미지 내에 포함시키는 과정입니다.
ADD ./build/libs/*.war appBatch.war 

#현재 디렉토리의 batchRun.sh 파일을 새로운 이미지 내부의 batchRun.sh 파일로 복사합니다.
ADD ./batchRun.sh batchRun.sh