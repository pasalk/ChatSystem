# Chat system

This is a rudimentary application for chatting between users on the same local network. 
When a user joins the network it discovers all online users on the network, as well as additional users who join the network later on.
To connect, a user chooses a username. The username must be unique among all currently connected users. 
When connected, a user can view a list of all currently connected users, and pick any of them to send a message to. 
A user can also load chat history with any of the users that are currently connected to the network. As they send or receive new messages, the chat history is automatically updated.

## Maven 

Apache Maven is a build automation tool used primarily for Java projects. This project uses Apache Maven.
To run this project you first have to install Maven on your machine. If you are using Linux operating system open a new terminal and execute the following commands:
```
mkdir -p ~/bin  # create a bin/ directory in your home
cd ~/bin  # jump to it
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O maven.tar.gz  # download maven
tar xf maven.tar.gz  # decompress it
echo 'export PATH=~/bin/apache-maven-3.9.5/bin:$PATH' >> ~/.bashrc  # add mvn's directory to the PATH
source ~/.bashrc  # reload terminal configuration
```
Once you have done that, you can check that Maven is now successfully installed on your machine using the following command:
```
mvn -v
```
Check that you pulled the latest version of code from Git.
Now you can chat-system directory which contains pom.xml file, compile and run your code using the following commands:
```
mvn compile
mvn package
mvn exec:java
```
or shorter:
```
mvn compile package exec:java
```
You can also run
```
mvn test
```
to run the tests.

## SSH

To test the code you should simulate a local network with multiple users. That is possible to do on a single machine using SSH protocol.
The Secure Shell Protocol (SSH) is a cryptographic network protocol for operating network services securely over an unsecured network. Its most notable applications are remote login and command-line execution.
Using ssh is very simple. After installing it, you simply need to run the following command:
```
ssh -X hostname
```
Instead of word hostname, write down the actual hostname of the machine you are trying to use. The hostname can be checked using the `hostname` command.
To check that the two machines are not communicating you can use commands `ifconfig` and `ping`.
Once, the machines are successfully communicating, you can set up Maven and run the code on the other machine as well.

## Usage
To efficiently test the system it is recommended to use at least three computers connected to the same local network.
The application can simply and intuitively be used via the graphical user interface (GUI).

Once the code is run, the GUI enters the connect view. When a user clicks the connect button, they are connecting to the chat application where other users can discover them.
If this is your first time running this application on your IP address, you will be asked to choose your username. If your username is the same as the username of one of the currently connected users, you will be asked to choose another username. On the other hand, if you have already used this application on your IP address, your old username will be stored locally and you will not be asked to choose a username. If, however, your old username is the same as the username of one of the currently connected users, you will be asked to change it.

After connecting to the network, the user is presented with the command selection view. Here, they can select to load a list of currently connected contacts, pick one to see their chat history as well as chat with them, change their username or disconnect from the chat application.

It is also important to note that, for correct functioning of the application, the application must be closed using the disconnect button or the red X in the corner. Closing the application in another way will result in a DISCONNECT messsage not being sent to the other users in the network which could potentially cause more problems.

Unfortunately, the application has a small bug. When a user is in the contact list view while another user enters or leaves the network, or changes their username, the contact list should rerender. Instead however, it rerenders an emplty frame. To avoit this issue, it is suggested not to add, rename or remove contacts while a user is in the contact list view.







