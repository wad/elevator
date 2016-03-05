package elevator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static elevator.ElevatorState.*;

/*
Notes:
You can't really tell if an elevator is occupied or not. So it just guesses.

todo: handle up/down external buttons
todo: handle case where all elevators are being serviced
 */

@SuppressWarnings("Duplicates")
public class Elevator
{
	static final int MAX_TRIPS_BEFORE_SERVICE = 100;
	static final int BOTTOM_FLOOR = 1;

	static final int UP = 1;
	static final int DOWN = -1;

	final int numFloors;
	final int elevatorNumber;

	ElevatorState elevatorState;
	int currentFloor;
	Set<Integer> floorsToStopOn;

	int numTripsSinceLastService = 0;
	int totalNumTrips = 0;
	int totalFloorsPassed = 0;

	// todo: This is ugly. Find a better way to handle this logic.
	boolean internalRequestReceivedSinceLastTick;

	Monitor monitor;

	public Elevator(
			Monitor monitor,
			int numFloors,
			int elevatorNumber)
	{
		this.elevatorNumber = elevatorNumber;
		this.monitor = monitor;
		this.numFloors = numFloors;
		floorsToStopOn = new HashSet<>(numFloors);

		currentFloor = 1;
		elevatorState = waitingForPassengers;
	}

	// Someone pushed a button from outside
	public void externalFloorRequest(int destinationFloor)
	{
		addFloorToStopOn(destinationFloor);
	}

	// Someone pushed a button from inside
	public void internalFloorRequest(int destinationFloor)
	{
		// Let's fix the state if needed
		switch (elevatorState)
		{
			case waitingForPassengers:
			case waitingForService:
			case movingUpWhileOccupied:
			case movingDownWhileOccupied:
				break;
			case movingUpWhileEmpty:
				elevatorState = movingUpWhileOccupied;
				break;
			case movingDownWhileEmpty:
				elevatorState = movingDownWhileOccupied;
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}

		internalRequestReceivedSinceLastTick = true;
		addFloorToStopOn(destinationFloor);
	}

	void addFloorToStopOn(int destinationFloor)
	{
		boolean elevatorIsStopped = !elevatorState.isGoingUp && !elevatorState.isGoingDown;
		if (elevatorIsStopped && destinationFloor == currentFloor)
			return;

		if (elevatorState.isGoingUp && destinationFloor < currentFloor)
			return;

		if (elevatorState.isGoingDown && destinationFloor > currentFloor)
			return;

		// handle the case where the elevator is in a middle floor, and someone pushes buttons both above and below
		if (!floorsToStopOn.isEmpty())
		{
			boolean selectedFloorIsAboveCurrentFloor = destinationFloor > currentFloor;
			boolean shouldDiscardRequest = shouldGoUp() != selectedFloorIsAboveCurrentFloor;
			if (shouldDiscardRequest)
				return;
		}

		floorsToStopOn.add(destinationFloor);
	}

	boolean shouldGoUp()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");

		int someFloorToStopOn = (int) floorsToStopOn.toArray()[0];
		return someFloorToStopOn > currentFloor;
	}

	int getHighestFloorToStopOn()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");
		return Collections.max(floorsToStopOn);
	}

	int getLowestFloorToStopOn()
	{
		if (floorsToStopOn.isEmpty())
			throw new IllegalStateException("Bug in code! Need to verify there are floors to stop on before calling me.");
		return Collections.min(floorsToStopOn);
	}

	public void serviceIsComplete()
	{
		if (elevatorState == waitingForService)
		{
			numTripsSinceLastService = 0;
			changeState(waitingForPassengers);
		}
	}

	void openDoors()
	{
		monitor.report("Open doors", elevatorNumber, currentFloor);
	}

	void closeDoors()
	{
		monitor.report("Close doors", elevatorNumber, currentFloor);
	}

	void move(int direction)
	{
		boolean goingUp = direction == UP;

		if (goingUp && currentFloor == numFloors)
		{
			changeState(waitingForPassengers);
			return;
		}

		if (!goingUp && currentFloor == BOTTOM_FLOOR)
		{
			changeState(waitingForPassengers);
			return;
		}

		int newCurrentFloor = currentFloor + direction;
		monitor.report("Moved " + (goingUp ? "up" : "down") + " one level to floor " + newCurrentFloor, elevatorNumber, currentFloor);

		totalFloorsPassed++;
		currentFloor = newCurrentFloor;
	}

	void changeState(ElevatorState newState)
	{
		// todo: Consolidate some of these cases.
		switch (newState)
		{
			case waitingForPassengers:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						// do nothing
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						closeDoors();
						break;
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case waitingForService:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						// do nothing
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Elevator not allowed to move while in service.");
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingUpWhileEmpty:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingDownWhileEmpty:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingUpWhileOccupied:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			case movingDownWhileOccupied:
				switch (elevatorState)
				{
					case waitingForPassengers:
					case waitingForService:
						incrementNumTrips();
						openDoors();
						break;
					case movingUpWhileEmpty:
					case movingDownWhileEmpty:
					case movingUpWhileOccupied:
					case movingDownWhileOccupied:
						throw new IllegalStateException("Bad state change. From " + elevatorState + " to " + newState);
					default:
						throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
				}
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + newState);
		}

		monitor.report("Changed state from " + elevatorState + " to " + newState, elevatorNumber, currentFloor);

		elevatorState = newState;
	}

	void incrementNumTrips()
	{
		numTripsSinceLastService++;
		totalNumTrips++;
	}

	/**
	 * These results could change, if riders push more buttons, so it's just minimum value.
	 *
	 * @return null if the elevator will not be able to respond.
	 */
	public Integer howManyFloorsAreYouFromBeingAbleToStopHere(int destinationFloor)
	{
		boolean destinationFloorIsAbove = destinationFloor > currentFloor;

		switch (elevatorState)
		{
			case waitingForPassengers:
				if (floorsToStopOn.isEmpty())
					return destinationFloorIsAbove ? destinationFloor - currentFloor : currentFloor - destinationFloor;

				if (shouldGoUp())
				{
					if (destinationFloorIsAbove)
						return destinationFloor - currentFloor;

					// It would need to go up, and then back down again.
					int highestFloorToStopOn = getHighestFloorToStopOn();
					int floorsToGoUp = highestFloorToStopOn - currentFloor;
					int floorsToGoDown = highestFloorToStopOn - destinationFloor;
					return floorsToGoUp + floorsToGoDown;
				}
				else
				{
					if (!destinationFloorIsAbove)
						return currentFloor - destinationFloor;

					// It would need to go down, and then back up again.
					int lowestFloorToStopOn = getLowestFloorToStopOn();
					int floorsToGoDown = currentFloor - lowestFloorToStopOn;
					int floorsToGoUp = destinationFloor - lowestFloorToStopOn;
					return floorsToGoDown + floorsToGoUp;
				}
			case waitingForService:
				return null;
			case movingUpWhileEmpty:
			case movingUpWhileOccupied:
			{
				if (destinationFloorIsAbove)
					return destinationFloor - currentFloor;

				// assume we already have stopping floors, otherwise why is it moving?
				// It would need to go up, and then back down again.
				int highestFloorToStopOn = getHighestFloorToStopOn();
				int floorsToGoUp = highestFloorToStopOn - currentFloor;
				int floorsToGoDown = highestFloorToStopOn - destinationFloor;
				return floorsToGoUp + floorsToGoDown;
			}
			case movingDownWhileEmpty:
			case movingDownWhileOccupied:
			{
				if (!destinationFloorIsAbove)
					return currentFloor - destinationFloor;

				// assume we already have stopping floors, otherwise why is it moving?
				// It would need to go down, and then back up again.
				int lowestFloorToStopOn = getLowestFloorToStopOn();
				int floorsToGoDown = currentFloor - lowestFloorToStopOn;
				int floorsToGoUp = destinationFloor - lowestFloorToStopOn;
				return floorsToGoDown + floorsToGoUp;
			}
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}
	}

	// Elevators can only move during a tick event.
	public void tick()
	{
		switch (elevatorState)
		{
			case waitingForPassengers:
				boolean elevatorNeedsToMove = !floorsToStopOn.isEmpty();
				if (elevatorNeedsToMove)
				{
					// It will actually move next tick. This tick just updates the state.
					if (shouldGoUp())
						changeState(internalRequestReceivedSinceLastTick ? movingUpWhileOccupied : movingUpWhileEmpty);
					else
						changeState(internalRequestReceivedSinceLastTick ? movingUpWhileOccupied : movingUpWhileEmpty);
				}
				else if (numTripsSinceLastService >= MAX_TRIPS_BEFORE_SERVICE)
					changeState(waitingForService);
				break;
			case waitingForService:
				break;
			case movingUpWhileEmpty:
			case movingUpWhileOccupied:
				if (floorsToStopOn.contains(currentFloor))
					changeState(waitingForPassengers);
				else
					move(UP);
				break;
			case movingDownWhileEmpty:
			case movingDownWhileOccupied:
				if (floorsToStopOn.contains(currentFloor))
					changeState(waitingForPassengers);
				else
					move(DOWN);
				break;
			default:
				throw new IllegalStateException("Bug in code! Unhandled state: " + elevatorState);
		}

		// Blank this out at the end of each tick.
		internalRequestReceivedSinceLastTick = false;
	}
}
