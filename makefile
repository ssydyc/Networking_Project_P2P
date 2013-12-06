JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	central_server.java \
	Client.java \
	my_function.java \
	peer.java \
	PeerInfo.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
