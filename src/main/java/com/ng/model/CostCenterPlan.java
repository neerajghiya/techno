
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name="CostCenterPlan")
@JsonRootName("CostCenterPlan")
public class CostCenterPlan implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 676608138681168964L;
	private String year="";
	private String status="";
	private String totalPlan="";
	private String createTime="";
	private CostCenters costCenters = new CostCenters();
	
	@XmlElement(name="Year")
	@JsonProperty("Year")
	public String getYear() {
		return year;
	}
	@XmlElement(name="Status")
	@JsonProperty("Status")
	public String getStatus() {
		return status;
	}
	@XmlElement(name="TotalPlan")
	@JsonProperty("TotalPlan")
	public String getTotalPlan() {
		return totalPlan;
	}
	@XmlElement(name="CreateTime")
	@JsonProperty("CreateTime")
	public String getCreateTime() {
		return createTime;
	}
	@XmlElement(name="CostCenters")
	@JsonProperty("CostCenters")
	public CostCenters getCostCenters() {
		return costCenters;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTotalPlan(String totalPlan) {
		this.totalPlan = totalPlan;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setCostCenters(CostCenters costCenters) {
		this.costCenters = costCenters;
	}
	
}
