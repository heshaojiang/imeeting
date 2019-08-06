import React, { PureComponent } from 'react';
import { fetchCustomerlist } from '@/services/customer';
import { Select } from 'antd';
import { formatMessage } from 'umi/locale';

// const FormItem = Form.Item;
const Option = Select.Option;
export default class SelectCustomer extends PureComponent {
  // static getDerivedStateFromProps(nextProps) {
  //   // Should be a controlled component.
  //   if ('value' in nextProps) {
  //     return {
  //       ...(nextProps.value || {}),
  //     };
  //   }
  //   return null;
  // }
  componentWillReceiveProps(nextProps) {
    if ('value' in nextProps) {
      const value = nextProps.value;
      this.setState({ value });
    }
  }

  constructor(props) {
    super(props);
    const value = props.value || {};
    this.state = {
      customerData: [],
      number: value.number || 0,
      currency: value.currency || 'rmb',
      value: value,
    };
  }
  componentDidMount() {
    if(this.state.customerData.length === 0) {
      this.getInitCustomer();
    }
  }
  getInitCustomer = () =>
    fetchCustomerlist().then(v => {
      this.setState({
        customerData: v
      });
    });

  onChangeSelect = (changedValue) => {
    // Should provide an event to pass value to Form.
    const onChange = this.props.onChange;
    if (onChange) {
      this.setState({ value: changedValue });
      // this.setState({
      //   value: changedValue
      // });
      // onChange(Object.assign({}, this.state, changedValue));
      onChange(changedValue);
    }
  }

  render() {
    const { value,customerData } = this.state;
    let customerid = null;
    if (typeof(value)=='number' || typeof(value)=='string') {
      customerid = Number(value);
    }
    let OptionsCustomer = null;
    if(customerData)
    {
      OptionsCustomer= customerData.map(el => <Option key={el.customerId} value={el.customerId}>{el.customerName}</Option>);
    }
    var props = {};
    if (customerid != null) {
      props.value = customerid;
    }
    return (
      <Select
        style={{ width: 295 }}
        { ...props }
        onFocus={this.getInitCustomer}
        onChange={this.onChangeSelect}
        placeholder={formatMessage({ id: 'app.admin.user.customer.select'})}
      >
        {OptionsCustomer}
      </Select>
    );
  }
}
