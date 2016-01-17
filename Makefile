SRC := CalendarCreator.java
DEP := jcommander.jar
OUT := $(patsubst %.java,%.class,$(SRC))
JAR := $(patsubst %.java,%.jar,$(SRC))

all: $(JAR)
$(JAR): $(OUT)
	cp -f $(DEP) $(JAR)
	jar ufe $@ $(patsubst %.java,%,$(SRC)) *.class
$(OUT): $(SRC)
	javac -classpath $(DEP) $<

clean:
	rm -f *.class *.png
	rm -f $(JAR)

.phony: clean
