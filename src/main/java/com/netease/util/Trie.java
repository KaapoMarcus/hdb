package com.netease.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Trie<K, V> {
	TrieNode root;
	
	public Trie() {
		root = new TrieNode();
	}
	
	
	public V get(K[] prefix) {
		TrieNode n = root;
		for (int i = 0; i < prefix.length; i++) {
			n = n.subMap.get(prefix[i]);
			if (n == null)
				return null;
		}
		return n.v;
	}
	
	
	public V get(Iterator<K> ki) {
		TrieNode n = root, sub;
		while (ki.hasNext()) {
			K k = ki.next();
			sub = n.subMap.get(k);
			if (sub == null)
				return n.v;
			n = sub;
		}
		return n.v;
	}
	
	
	public V put(K[] prefix, V v) {
		TrieNode n = root, sub;
		for (int i = 0; i < prefix.length; i++) {
			sub = n.subMap.get(prefix[i]);
			if (sub == null) {
				sub = new TrieNode();
				n.subMap.put(prefix[i], sub);
			}
			n = sub;
		}
		V oldV = n.v;
		n.v = v;
		return oldV;
	}
	
	
	class TrieNode {
		V v;
		Map<K, TrieNode> subMap;
		
		TrieNode() {
			v = null;
			subMap = new HashMap<K, TrieNode>();
		}
	}
}
