package elevator;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController
{
	int timeCounter = 0;
	Monitor monitor = new Monitor();

	List<Elevator> elevators;

	public ElevatorController(
			int numElevators,
			int numFloors)
	{
		if (numElevators < 1)
			throw new IllegalArgumentException("Need at least one elevator.");

		if (numFloors < 2)
			throw new IllegalArgumentException("Need at least two floors.");

		elevators = new ArrayList<>(numElevators);
		for (int i = 0; i < numElevators; i++)
			elevators.add(new Elevator(monitor, numFloors, i));

		monitor.report("Elevator controller started with " + numElevators + " elevators and " + numFloors + " floors");
	}

	public void tick()
	{
		monitor.report("tick " + timeCounter);

		for (Elevator elevator : elevators)
			elevator.tick();
		timeCounter++;
	}

	public void callForRide(
			int fromFloor,
			boolean wantsToGoUp)
	{
		monitor.report("Person on floor " + fromFloor + " pushed the " + (wantsToGoUp ? "up" : "down") + " button");

		// todo: figure out which elevator can answer
		// todo: set that elevator's state, so that next tick it will react
	}

	public void riderRequest(
			int elevatorNumber,
			int toFloor)
	{
		monitor.report("Rider in elevator " + elevatorNumber + " pushed floor button " + toFloor);

		// todo: figure out which elevator can answer
		// todo: set that elevator's state, so that next tick it will react
	}

	public void markServiceIsComplete(int elevatorNumber)
	{
		monitor.report("Service performed", elevatorNumber);
		elevators.get(elevatorNumber).serviceIsComplete();
	}
}
