package net.daum.clix.springframework.data.rest.client.json.domain;

public class Cat extends Animal {

	private int lives;

	public Cat() {
	}

	public Cat(String name) {
		super(name);
	}

	public Cat(String name, int lives) {
		super(name);
		this.lives = lives;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lives;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cat other = (Cat) obj;
		if (lives != other.lives)
			return false;
		return true;
	}
	

}
