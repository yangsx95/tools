# README

## 更改部门id所受的影响

以下是涉及到的所有的表：

```yaml
tables: 
    # 交付申请
    - t_delivery_apply: 
      - dept_id
    # 销售订单
    - t_sales_order: 
      - CREATE_DEPT_ID
    # 交付记录
    - t_delivery_update_record: 
      - dept_id
    # 标的物方案
    - t_subject_matter_scheme: 
      - CREATE_DEPT_ID
    # 审批流中间表
    - t_activity_middle: 
      - department_id
    # 现金流冻结履历
    - t_cash_flow_freeze_history: 
      - department_id
    # 业务表
    - t_contract: 
      - department_id
    # 合同归档申请表
    - t_contract_archive_apply: 
      - DEPARTMENT_ID
    # 电量套餐明细
    - t_electricity_package: 
      - department_id
    # 内部租赁
    - t_inner_lease: 
      - department_id
    # 开票
    - t_invoice_apply: 
      - department_id
    # 租售订单表
    - t_lease_order: 
      - CREATE_DEPART_ID
    # 物流任务
    - t_logistics_task: 
      - PLACE_OF_DEPARTURE
    # 决策表
    - t_motion: 
      - department_id
    # 续签表
    - t_renew_delivery: 
      - CREATE_DEPART_ID
    # 电站扩展信息表
    - t_station_extend_info: 
      - department_id
    # 开工表
    - t_station_start_build: 
      - department_id

json-fields:
  - t_change: 
    
  - t_business_json:
      
  - t_business_snapshot:

```