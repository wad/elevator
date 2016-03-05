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

	public void callForRide(
			int fromFloor,
			boolean wantsToGoUp)
	{
		elevatorController.callForRide(fromFloor, wantsToGoUp);
	}

	public void riderRequest(
			int elevatorNumber,
			int toFloor)
	{
		elevatorController.riderRequest(elevatorNumber, toFloor);
	}

	public void performService(int elevatorNumber)
	{
		elevatorController.markServiceIsComplete(elevatorNumber);
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

		ElevatorSimulation sim = new ElevatorSimulation(numElevators, numFloors);
		sim.tick();
		sim.tick();
		sim.tick();
	}
}
