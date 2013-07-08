package server;

public class ReaderManager implements Runnable {

	@Override
	public void run() {
		try {
			String filename = System.getProperty("user.dir")
					+ "/src/server/temp.rss";
			int max = 50;
			while (true) {
				TwitterReader tr = new TwitterReader(filename, max);
				tr.go();
				while (!tr.isDone()) {
					int millis = 10000;
					System.out.println("TR not done yet. Sleeping for " + millis/1000 + " seconds.");
					Thread.sleep(millis);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
