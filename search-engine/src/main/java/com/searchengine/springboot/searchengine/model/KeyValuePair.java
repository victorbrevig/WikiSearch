package com.searchengine.springboot.searchengine.model;

public class KeyValuePair<G,T extends Comparable<T>> implements Comparable<KeyValuePair<G,T>> {

	public G key;
	public T value;
	
	public KeyValuePair(G key, T value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		if (!(o instanceof KeyValuePair)) {
			return false;
		}
		
		KeyValuePair<G,T> keyValPair = (KeyValuePair<G,T>) o;
		
		return this.key.equals(keyValPair.key);
		
	}
	
    @Override
    public int hashCode() {
    	// IDEA: https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
    	/*
        int result = 17;
        result = 31 * result + (int) (key ^ (key >>> 32));
        return result;
        */
    	return key.hashCode();
    }

	@Override
	public int compareTo(KeyValuePair<G, T> o) {

		return value.compareTo(o.value);
	}
	
}
