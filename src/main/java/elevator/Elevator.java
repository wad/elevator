package elevator;

public class Elevator
{
	int numFloors;
	int currentFloor;
	ElevatorState elevatorState;

	public Elevator(int numFloors)
	{
		this.numFloors = numFloors;
		currentFloor = 1;
		elevatorState = ElevatorState.waitingForPassengers;
	}
}
