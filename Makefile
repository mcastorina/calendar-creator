SRC := CalendarCreator.java
OUT := $(patsubst %.java,%.class,$(SRC))

all: $(OUT)
$(OUT): $(SRC)
	javac $<

clean:
	rm -f *.class *.png

.phony: clean
