package net.daum.clix.springframework.data.rest.client.json.domain;

public class Dog extends Animal {

	private double barkVolume;

	public Dog() {
	}
	
	public Dog(String name) {
		super(name);
	}

	public Dog(String name, double barkVolume) {
		super(name);
		this.barkVolume = barkVolume;
	}

	public double getBarkVolume() {
		return barkVolume;
	}

	public void setBarkVolume(double barkVolume) {
		this.barkVolume = barkVolume;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(barkVolume);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Dog other = (Dog) obj;
		if (Double.doubleToLongBits(barkVolume) != Double.doubleToLongBits(other.barkVolume))
			return false;
		return true;
	}
	
	
}
