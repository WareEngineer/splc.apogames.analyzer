package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grapher.Grapher;

public class OverlapedArchitecture {
	private Set<String> totalGameTitles = new HashSet<String>();
	private Map<String, Set<String>> totalCloneInfo = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> totalReuseInfo = new HashMap<String, Set<String>>();
	private Map<String, OverlapedClass> olClassInfo = new HashMap<String, OverlapedClass>();
	private Map<String, Set<String>> olCallRelationInfo = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> olExtendRelationInfo = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> olImplementRelationInfo = new HashMap<String, Set<String>>();
	private Map<String, Double> tcciInfo = new HashMap<String, Double>();

	public Map<String, Set<String>> getReuseInfo() {
		return totalReuseInfo;
	}
	public Map<String, Set<String>> getCallRelations() {
		return olCallRelationInfo;
	}
	public Map<String, Set<String>> getExtendRelations() {
		return olExtendRelationInfo;
	}
	public Map<String, Set<String>> getImplementRelations() {
		return olImplementRelationInfo;
	}
	public Set<String> getTitleInfo() {
		return totalGameTitles;
	}
	public Map<String, Set<String>> getCloneInfo() {
		return totalCloneInfo;
	}
	public Map<String, OverlapedClass> getClassInfo() {
		return olClassInfo;
	}
	public Map<String, Double> getTcciInfo() {
		return tcciInfo;
	}
	
	public void setTcciInfo(Map<String, Double> tccis) {
		this.tcciInfo = tccis;
	}

	public void addTitle(String title) {
		totalGameTitles.add(title);
	}
	public void addCallRelationInfo(String title, Set<String> callRelations) {
		this.buildMap(olCallRelationInfo, callRelations, title);
	}
	public void addExtendRelationInfo(String title, Set<String> extendRelations) {
		this.buildMap(olExtendRelationInfo, extendRelations, title);
	}
	public void addImplementRelationInfo(String title, Set<String> implementRelations) {
		this.buildMap(olImplementRelationInfo, implementRelations, title);
	}

	private void buildMap(Map<String, Set<String>> map, Set<String> keys, String value) {
		for(String key : keys) {
			if(!map.containsKey(key)) {
				map.put(key, new HashSet<String>());
			}
			map.get(key).add(value);
		}
	}
	
	public void addCloneInfo(String title, ClassModel cm) {
		String cName = cm.getFullName();
		if(totalCloneInfo.containsKey(cName)==false) {
			totalCloneInfo.put(cName, new HashSet<String>());
		}
		totalCloneInfo.get(cName).add(title);
	}
	
	public void addReuseInfo(String title, ClassModel cm) {
		String cName = cm.getFullName();
		if(totalReuseInfo.containsKey(cName) == false) {
			totalReuseInfo.put(cName, new HashSet<String>());
			olClassInfo.put(cName, new OverlapedClass(cName));
		}
		totalReuseInfo.get(cName).add(title);
		olClassInfo.get(cName).overlab(title, cm);
	}

}


