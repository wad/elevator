package elevator;

import java.util.ArrayList;
import java.util.List;

public class ElevatorController
{
	int timeCounter = 0;

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
			elevators.add(new Elevator(numFloors));
	}

	public void tick()
	{
		timeCounter++;
		// todo
	}
}
