	1. CI-CD Shared libraries - https://github.com/VijaySHegde/cicd-sharedlibrary Here I have committed shared libraries used in my pipeline. The pipeline itself is a shared library as per DRY principle
	
	2. Spring Pet clinic application - https://github.com/VijaySHegde/spring-petclinic The forked repo which contains Jenkinsfile, deployment.yml, service.yml, docker-compose and playbook.yml files with source code
	
	3. Terraform use case - https://github.com/VijaySHegde/terraform-demo I have used terraform to create a ec2 instance of t2.micro linux ami with .tf files by referring official document
	
	4. Docker Hub - https://hub.docker.com/u/vijayshegde Docker Hub is used to store the images built as part of the build stage.
	
The flow diagram looks like this:

	• The main pipeline cicd-sharedlibrary/cicd.groovy at dev · VijaySHegde/cicd-sharedlibrary (github.com) which refers to Jenkinsfile (Config file) is also a shared library as per DRY principle.
	
	• spring-petclinic/Jenkinsfile at main · VijaySHegde/spring-petclinic (github.com) - Jenkinsfile is designed in such a way that it can be utilized by any app team and they can enable and disable the various stages based on Yes/No Boolean value.
	
	• This helps is debugging faster by quickly disabling some stages in the pipeline
	
	• Dockerfile ( spring-petclinic/Dockerfile at main · VijaySHegde/spring-petclinic (github.com) ) is written to build the petclinic app image as it is container based deployment.
	
	• Docker-compose file ( spring-petclinic/docker-compose.yml at main · VijaySHegde/spring-petclinic (github.com) ) is given by official team is used with MySQL profile so that application connects to separate DB container.
	
	• Ansible playbook is used to deploy the image with help of yml template service.yml and Deployment.yml
	
	• I have written combined k8s config file kubernetes-configmap.yml which is used to deploy into k8s cluster which is enable is EKS.
	• EKS is enabled with one master node and two worker nodes for our pipeline
	
![image](https://user-images.githubusercontent.com/55663295/123197059-9a2b2a80-d4c8-11eb-8fa9-72b889d16fde.png)
