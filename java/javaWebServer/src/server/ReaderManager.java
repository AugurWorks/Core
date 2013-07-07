package server;

public class ReaderManager implements Runnable {

	@Override
	public void run() {
		try {
			String filename = System.getProperty("user.dir")
					+ "/src/server/temp.rss";
			Thread t = new Thread();
			while (true) {
				if (t.isAlive()) {
					Thread.sleep(5000);
				} else {
					t = new Thread(new TwitterReader(filename, 5));
					t.start();
					Thread.sleep(5000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}