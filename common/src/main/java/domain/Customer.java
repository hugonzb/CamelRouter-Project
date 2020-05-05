package domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Customer implements Serializable {

	private String id;

	@SerializedName("customer_group_id")
	private String group;

	private String email;

	public Customer() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "Customer{" + "id=" + id + ", group=" + group + ", email=" + email + '}';
	}

}
