import { useCallback } from 'react';
import styled from 'styled-components';

import { colors } from '../../../config.json';
import { AriaInput } from '../AriaGroup';
import { smallNormalText } from '../common-styles';

const StyledInput = styled.input(smallNormalText, {
  flex: 1,
  textAlign: 'right',
  background: 'transparent',
  border: 'none',
  color: colors.white,

  '&&::placeholder': {
    color: colors.white50,
  },
});

interface SettingsTextInputProps extends InputProps<'text'> {
  defaultValue?: string;
}

export function SettingsTextInput(props: SettingsTextInputProps) {
  return <Input type="text" {...props} />;
}

interface SettingsNumberInputProps extends Omit<InputProps<'number'>, 'onUpdate'> {
  defaultValue?: number;
  onUpdate: (value: number) => void;
}

export function SettingsNumberInput(props: SettingsNumberInputProps) {
  const { onUpdate, ...otherProps } = props;
  const onNumberUpdate = useCallback(
    (value: string) => {
      onUpdate(parseInt(value));
    },
    [onUpdate],
  );

  return <Input {...otherProps} onUpdate={onNumberUpdate} />;
}

type ValueTypes = 'text' | 'number';
type ValueType<T extends ValueTypes> = T extends 'number' ? number : string;

interface InputProps<T extends ValueTypes> extends React.HTMLAttributes<HTMLInputElement> {
  type?: T;
  value?: ValueType<T>;
  defaultValue?: ValueType<T>;
  onUpdate: (value: string) => void;
}

function Input<T extends ValueTypes>(props: InputProps<T>) {
  const { onUpdate, ...otherProps } = props;

  const onChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      props.onChange?.(event);
      props.onUpdate(event.target.value);
    },
    [props.onUpdate, props.onChange],
  );

  return (
    <AriaInput>
      <StyledInput {...otherProps} onChange={onChange} />
    </AriaInput>
  );
}
