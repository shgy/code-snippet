```
echo deb http://get.docker.com/ubuntu docker main | sudo tee /etc/apt/sources.list.d/docker.list

sudo apt-key adv --keyserver pgp.mit.edu --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9

sudo apt-get update
sudo apt-get install -y lxc-docker-1.3.3
```
