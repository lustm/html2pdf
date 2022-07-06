### html2pdf

mvn clean && mvn package

docker build -t htmltopdf .

docker run -d -p 80:80 --name htmltopdf --restart unless-stopped htmltopdf

### convert test
curl -L -o baidu.pdf -d 'url=https://www.baidu.com' -XPOST http://localhost/urlToPdf
