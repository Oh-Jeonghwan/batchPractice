docker run -itd --name docker_batch_app \
	  --network docker_batch_netwokr \
	  -e SPRING_PROFILES_ACTIVE=docker \
	  -v /APP/batch/R102/INFILES:/INFILES \
	  docker_batch_app_image