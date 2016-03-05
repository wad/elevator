package elevator;

import static elevator.ElevatorState.*;

public class Elevator
{
	final int numFloors;
	final int elevatorNumber;

	ElevatorState elevatorState;
	int currentFloor;
	int destinationFloor;

	Monitor monitor;

	public Elevator(
			Monitor monitor,
			int numFloors,
			int elevatorNumber)
	{
		this.elevatorNumber = elevatorNumber;
		this.monitor = monitor;
		this.numFloors = numFloors;
		currentFloor = 1;
		destinationFloor = currentFloor;
		elevatorState = waitingForPassengers;
	}

	void openDoors()
	{
		monitor.report("Open doors", elevatorNumber, currentFloor);
	}

	void changeState(ElevatorState newState)
	{
		switch(newState)
		{
			case waitingForPassengers:
				switch(elevatorState)
				{
					case waitingForPassengers:
						break;
					case waitingForService:
						break;
					case movingUpWhileEmpty:
						break;
					case movingDownWhileEmpty:
						break;
					case movingUpWhileOccupied:
						break;
					case movingDownWhileOccupied:
						break;
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case waitingForService:
				break;
			case movingUpWhileEmpty:
				break;
			case movingDownWhileEmpty:
				break;
			case movingUpWhileOccupied:
				break;
			case movingDownWhileOccupied:
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + newState);
		}
		elevatorState = newState;
	}

	public void tick()
	{
		switch(elevatorState)
		{
			case waitingForPassengers:
			case waitingForService:
				break;
			case movingUpWhileEmpty:
				if (currentFloor == destinationFloor)
					changeState(waitingForPassengers);
				else if (currentFloor < destinationFloor)
					currentFloor++;
				break;
			case movingDownWhileEmpty:
				break;
			case movingUpWhileOccupied:
				break;
			case movingDownWhileOccupied:
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}
	}
}
