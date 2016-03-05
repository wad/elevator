package elevator;

// todo: Can make this a singleton (easy when using Spring)
public class Monitor
{
	public void report(String message)
	{
		System.out.println(message);
	}

	public void report(
			String message,
			int elevatorNumber)
	{
		report("Elevator " + elevatorNumber + ": " + message);
	}

	public void report(
			String message,
			int elevatorNumber,
			int floorNumber)
	{
		report("Elevator " + elevatorNumber + " on floor " + floorNumber + ": " + message);
	}
}
