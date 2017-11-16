package br.unicamp.ic.lis.ontomatch.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Resource {
	private String uri;
	private String label;
	private double similarity;
	
	private List<Resource> parents = new ArrayList<>();
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	@Override
	public String toString() {
		return "Resource [uri=" + uri + ", label=" + label + ", similarity=" + similarity + ", parents=" + parents
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Resource other = (Resource) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	public List<Resource> getParents() {
		return parents;
	}
	public void setParents(List<Resource> parents) {
		this.parents = parents;
	}
}
