set datafile separator ','
plot 'ElevatorTest.csv' using 1:2 with points title columnhead, \
	'ElevatorTest.csv' using 1:3 with points title columnhead, \
	'ElevatorTest.csv' using 1:4 with points title columnhead, \
	'ElevatorTest.csv' using 1:5 with points title columnhead, \
	'ElevatorTest.csv' using 1:6 with points title columnhead, \
	'ElevatorTest.csv' using 1:7 with points title columnhead
