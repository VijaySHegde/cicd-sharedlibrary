# cicd-demo

GO TO Dev BRANCH -Shared libraries

	1) CI-CD Shared libraries - https://github.com/VijaySHegde/cicd-sharedlibrary Here I have committed shared libraries used in my pipeline. The pipeline itself is a shared library as per DRY principle
	
	2) Spring Pet clinic application - https://github.com/VijaySHegde/spring-petclinic The forked repo which contains Jenkinsfile, deployment.yml, service.yml, docker-compose and playbook.yml files with source code
	
	3) Terraform use case - https://github.com/VijaySHegde/terraform-demo I have used terraform to create a ec2 instance of t2.micro linux ami with .tf files by referring official document
	
	4) Docker Hub - https://hub.docker.com/u/vijayshegde Docker Hub is used to store the images built as part of the build stage.
The flow diagram looks like this:
	


	• The main pipeline cicd-sharedlibrary/cicd.groovy at dev · VijaySHegde/cicd-sharedlibrary (github.com) which refers to Jenkinsfile (Config file) is also a shared library as per DRY principle.
	
	• spring-petclinic/Jenkinsfile at main · VijaySHegde/spring-petclinic (github.com) - Jenkinsfile is designed in such a way that it can be utilized by any app team and they can enable and disable the various stages based on Yes/No Boolean value.
	
	• This helps is debugging faster by quickly disabling some stages in the pipeline
	
	• Dockerfile ( spring-petclinic/Dockerfile at main · VijaySHegde/spring-petclinic (github.com) ) is written to build the petclinic app image as it is container based deployment.
	
	• Docker-compose file ( spring-petclinic/docker-compose.yml at main · VijaySHegde/spring-petclinic (github.com) ) is given by official team is used with MySQL profile so that application connects to separate DB container.
	
	• Ansible playbook is used to deploy the image with help of yml template service.yml and Deployment.yml
	
	• I have written combined k8s config file kubernetes-configmap.yml which is used to deploy into k8s cluster which will be enabled in EKS.
	
	• EKS is enabled with one master node and two worker nodes for our pipeline

Webhook:
	• Github webhook is enabled in petclinic repo and terraform repo - whenever some change events like merge, push happes, Jenkins will trigger the job using webhook post  request.
	Webhooks (github.com)
	
	Request URL: http://65.2.147.47:8081
Request method: POST
Accept: */*
content-type: application/json
User-Agent: GitHub-Hookshot/bb7ff82
X-GitHub-Delivery: 83a2a288-d49c-11eb-806a-d3d7af186eeb
X-GitHub-Event: push
X-GitHub-Hook-ID: 304066063
X-GitHub-Hook-Installation-Target-ID: 379577797
X-GitHub-Hook-Installation-Target-Type: repository
	
	Unit test execution- it is done by Junit. There are around 40 unit tests cases which are there and passed
	
	Code coverage - I have used jacoco for code coverage.

	SonarQube for static quality analysis and NexusIQ - I have written code for sonarscan and security scanning but haven't integrated with sonar instance as it needs large memory 
	
	

	

	
![image](https://user-images.githubusercontent.com/55663295/123207820-9ce34b00-d4db-11eb-9366-76bbfce772ce.png)

http://65.2.147.47:8090/
![image](https://user-images.githubusercontent.com/55663295/123213881-2d258e00-d4e4-11eb-8aac-18c8a7a739e5.png)



https://github.com/VijaySHegde/terraform-demo/blob/main/README.md terraform 

