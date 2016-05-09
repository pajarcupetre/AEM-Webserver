# AEM-Webserver
A multi-threaded ( file-based) web server with thread-pooling

This server will receive HTTP request version HTTP1.1 for downloading a local file based on his name and upload a file to server.

The request for downloading a file will be based on a parameter "filename"

Example request:

-> curl localhost:8080?filename=powerlawTest.py -v -H "Connection: keep-alive"

will try to download this file from server.

For upload a file we need to a post using multipart/formdata contentType example in tests:postHeadersForUpload postBody

Files on server are saved on aem.webserver.fileUploadPath defined on application properties config file.
In this file we can define the number of threads that will be used under ThreadPool and the port that will used.

Running the webserver:
mvn clean install
cd target
java -cp webserver-1.0-SNAPSHOT.jar com.aem.runner.WebServerApp (this implies running on default 8080 with 20 threads)  
java -cp webserver-1.0-SNAPSHOT.jar com.aem.runner.WebServerApp 8090 40

Tests are running on commit on travis:
https://travis-ci.org/pajarcupetre/AEM-Webserver

Starting points:

https://www.safaribooksonline.com/library/view/http-the-definitive/1565925092/ch04s05.html
http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html


