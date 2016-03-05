package elevator;

public class ElevatorSimulation
{
	ElevatorController elevatorController;

	public ElevatorSimulation(
			int numElevators,
			int numFloors)
	{
		elevatorController = new ElevatorController(numElevators, numFloors);
	}

	public void tick()
	{
		elevatorController.tick();
	}

	public static void main(String... args)
	{
		System.out.println("First argument is how many elevators, second argument is how many floors.");

		// Just some default values for convenience while developing
		int numElevators = 3;
		int numFloors = 10;

		if (args.length == 2)
		{
			numElevators = Integer.parseInt(args[0]);
			numFloors = Integer.parseInt(args[1]);
		}

		ElevatorSimulation elevatorSimulation = new ElevatorSimulation(numElevators, numFloors);
	}
}
