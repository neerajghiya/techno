
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name="CostCenter")
@JsonRootName("CostCenter")
public class CostCenter {
	private String costCenterNumber="";
	private String description="";
	private String manager="";
	private String entered="";
	private String difference="";
	private String planTotal="";
	private String status="";
	private InternalOrderSummary internalOrderSummary;
	private String createTime="";
	private String additions="";
	private String efficiencies="";
	private String cyPlan="";
	
	public CostCenter(){
		
	}
	
	@XmlElement(name="CostCenterNumber")
	@JsonProperty("costCenter")
	public String getCostCenterNumber() {
		return costCenterNumber;
	}
	@XmlElement(name="Description")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
	@XmlElement(name="Manager")
	@JsonProperty("manager")
	public String getManager() {
		return manager;
	}
	@XmlElement(name="Entered")
	@JsonProperty("entered")
	public String getEntered() {
		return entered;
	}
	@XmlElement(name="Difference")
	@JsonProperty("difference")
	public String getDifference() {
		return difference;
	}
	@XmlElement(name="PYPlan")
	@JsonProperty("planTotal")
	public String getPlanTotal() {
		return planTotal;
	}
	@XmlElement(name="Status")
	@JsonProperty("status")
	public String getStatus() {
		return status;
	}
	
	@XmlElement(name="IOS", required=false)
	@JsonProperty("IOS")
	@JsonInclude(Include.NON_NULL)
	public InternalOrderSummary getInternalOrderSummary() {
		return internalOrderSummary;
	}
	
	@XmlElement(name="CreateTime", required=false)
	@JsonProperty("CreateTime")
	@JsonInclude(Include.NON_NULL)
	public String getCreateTime() {
		return createTime;
	}
	@XmlElement(name="Additions")
	@JsonProperty("additions")
	public String getAdditions() {
		return additions;
	}
	@XmlElement(name="Efficiencies")
	@JsonProperty("efficiencies")
	public String getEfficiencies() {
		return efficiencies;
	}
	@XmlElement(name="PlanTotal")
	@JsonProperty("cyPlan")
	public String getCyPlan() {
		return cyPlan;
	}
	public void setAdditions(String additions) {
		this.additions = additions;
	}
	public void setEfficiencies(String efficiencies) {
		this.efficiencies = efficiencies;
	}
	public void setCyPlan(String cyPlan) {
		this.cyPlan = cyPlan;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setInternalOrderSummary(InternalOrderSummary internalOrderSummary) {
		this.internalOrderSummary = internalOrderSummary;
	}
	public void setCostCenterNumber(String costCenterNumber) {
		this.costCenterNumber = costCenterNumber;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public void setEntered(String entered) {
		this.entered = entered;
	}
	public void setDifference(String difference) {
		this.difference = difference;
	}
	public void setPlanTotal(String planTotal) {
		this.planTotal = planTotal;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
