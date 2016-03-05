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
		elevatorController.addRequest(new ElevatorCallRequest(fromFloor, wantsToGoUp));
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

	public void finalReport()
	{
		elevatorController.finalReport();
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
		sim.riderRequest(0, 10);
		sim.riderRequest(1, 5);
		sim.tick();
		sim.callForRide(3, true);
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.tick();
		sim.finalReport();

/*
Output for this run:

/home/wad/apps/java/bin/java -Didea.launcher.port=7535 -Didea.launcher.bin.path=/home/wad/apps/idea-IU-143.1821.5/bin -Dfile.encoding=UTF-8 -classpath /home/wad/apps/java/jre/lib/jfr.jar:/home/wad/apps/java/jre/lib/jsse.jar:/home/wad/apps/java/jre/lib/javaws.jar:/home/wad/apps/java/jre/lib/management-agent.jar:/home/wad/apps/java/jre/lib/rt.jar:/home/wad/apps/java/jre/lib/deploy.jar:/home/wad/apps/java/jre/lib/plugin.jar:/home/wad/apps/java/jre/lib/charsets.jar:/home/wad/apps/java/jre/lib/jce.jar:/home/wad/apps/java/jre/lib/jfxswt.jar:/home/wad/apps/java/jre/lib/resources.jar:/home/wad/apps/java/jre/lib/ext/sunpkcs11.jar:/home/wad/apps/java/jre/lib/ext/jfxrt.jar:/home/wad/apps/java/jre/lib/ext/cldrdata.jar:/home/wad/apps/java/jre/lib/ext/sunec.jar:/home/wad/apps/java/jre/lib/ext/sunjce_provider.jar:/home/wad/apps/java/jre/lib/ext/localedata.jar:/home/wad/apps/java/jre/lib/ext/nashorn.jar:/home/wad/apps/java/jre/lib/ext/dnsns.jar:/home/wad/apps/java/jre/lib/ext/zipfs.jar:/home/wad/src/elevator/out/production/elevator:/home/wad/apps/idea-IU-143.1821.5/lib/idea_rt.jar com.intellij.rt.execution.application.AppMain elevator.ElevatorSimulation
First argument is how many elevators, second argument is how many floors.
Elevator controller started with 3 elevators and 10 floors
tick 0
Elevator 0 on floor 1: Elevator state waitingForPassengers
Elevator 1 on floor 1: Elevator state waitingForPassengers
Elevator 2 on floor 1: Elevator state waitingForPassengers
Rider in elevator 0 pushed floor button 10
Rider in elevator 1 pushed floor button 5
tick 1
Elevator 0 on floor 1: Close doors
Elevator 0 on floor 1: Changed state from waitingForPassengers to movingUpWhileOccupied
Elevator 0 on floor 1: Elevator state movingUpWhileOccupied
Elevator 1 on floor 1: Close doors
Elevator 1 on floor 1: Changed state from waitingForPassengers to movingUpWhileOccupied
Elevator 1 on floor 1: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Elevator state waitingForPassengers
Adding request: Person on floor 3 pushed the up button
tick 2
Handling request: Person on floor 3 pushed the up button
Elevator 2 was selected to receive call request.
Elevator 0 on floor 1: Moved up one level to floor 2
Elevator 0 on floor 2: Elevator state movingUpWhileOccupied
Elevator 1 on floor 1: Moved up one level to floor 2
Elevator 1 on floor 2: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Close doors
Elevator 2 on floor 1: Changed state from waitingForPassengers to movingUpWhileEmpty
Elevator 2 on floor 1: Elevator state movingUpWhileEmpty
tick 3
Elevator 0 on floor 2: Moved up one level to floor 3
Elevator 0 on floor 3: Elevator state movingUpWhileOccupied
Elevator 1 on floor 2: Moved up one level to floor 3
Elevator 1 on floor 3: Elevator state movingUpWhileOccupied
Elevator 2 on floor 1: Moved up one level to floor 2
Elevator 2 on floor 2: Elevator state movingUpWhileEmpty
tick 4
Elevator 0 on floor 3: Moved up one level to floor 4
Elevator 0 on floor 4: Elevator state movingUpWhileOccupied
Elevator 1 on floor 3: Moved up one level to floor 4
Elevator 1 on floor 4: Elevator state movingUpWhileOccupied
Elevator 2 on floor 2: Moved up one level to floor 3
Elevator 2 on floor 3: Elevator state movingUpWhileEmpty
tick 5
Elevator 0 on floor 4: Moved up one level to floor 5
Elevator 0 on floor 5: Elevator state movingUpWhileOccupied
Elevator 1 on floor 4: Moved up one level to floor 5
Elevator 1 on floor 5: Elevator state movingUpWhileOccupied
Elevator 2 on floor 3: Open doors
Elevator 2 on floor 3: Changed state from movingUpWhileEmpty to waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 6
Elevator 0 on floor 5: Moved up one level to floor 6
Elevator 0 on floor 6: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Open doors
Elevator 1 on floor 5: Changed state from movingUpWhileOccupied to waitingForPassengers
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 7
Elevator 0 on floor 6: Moved up one level to floor 7
Elevator 0 on floor 7: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 8
Elevator 0 on floor 7: Moved up one level to floor 8
Elevator 0 on floor 8: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 9
Elevator 0 on floor 8: Moved up one level to floor 9
Elevator 0 on floor 9: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 10
Elevator 0 on floor 9: Moved up one level to floor 10
Elevator 0 on floor 10: Elevator state movingUpWhileOccupied
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
tick 11
Elevator 0 on floor 10: Open doors
Elevator 0 on floor 10: Changed state from movingUpWhileOccupied to waitingForPassengers
Elevator 0 on floor 10: Elevator state waitingForPassengers
Elevator 1 on floor 5: Elevator state waitingForPassengers
Elevator 2 on floor 3: Elevator state waitingForPassengers
Elevator 0: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=9
Elevator 1: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=4
Elevator 2: Final report: numTripsSinceLastService=1 totalNumTrips=1 totalFloorsPassed=2
*/
	}
}
