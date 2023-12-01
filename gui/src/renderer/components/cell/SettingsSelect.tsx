import { useCallback, useEffect, useRef, useState } from 'react';
import styled from 'styled-components';

import { colors } from '../../../config.json';
import { useScheduler } from '../../../shared/scheduler';
import { useBoolean } from '../../lib/utilityHooks';
import { AriaInput } from '../AriaGroup';
import { smallNormalText } from '../common-styles';
import ImageView from '../ImageView';

export interface SettingsSelectItem<T extends string> {
  value: T;
  label: string;
}

const StyledSelect = styled.div.attrs({ tabIndex: 0 })(smallNormalText, {
  display: 'flex',
  alignItems: 'center',
  flex: 1,
  position: 'relative',
  background: 'transparent',
  border: 'none',
  color: colors.white,
  borderRadius: '4px',
  height: '26px',

  '&&:focus': {
    outline: `1px ${colors.darkBlue} solid`,
  },
});

const StyledItems = styled.div({
  position: 'absolute',
  top: 'calc(100% + 4px)',
  right: 0,
  backgroundColor: colors.darkBlue,
  border: `1px ${colors.darkerBlue} solid`,
  borderRadius: '4px',
  padding: '4px 8px',
});

const StyledSelectedContainer = styled.div({
  display: 'flex',
  alignItems: 'center',
});

const StyledChevron = styled(ImageView)({
  marginLeft: '6px',
});

interface SettingsSelectProps<T extends string> {
  defaultValue?: T;
  items: Array<SettingsSelectItem<T>>;
  onUpdate: (value: T) => void;
}

export function SettingsSelect<T extends string>(props: SettingsSelectProps<T>) {
  const [value, setValue] = useState<T>(props.defaultValue ?? props.items[0]?.value ?? '');
  const [itemsVisible, , closeItems, toggleItems] = useBoolean();

  const searchRef = useRef<string>('');
  const searchClearScheduler = useScheduler();

  const onSelect = useCallback((value: T) => {
    setValue(value);
    closeItems();
  }, []);

  const onKeyDown = useCallback(
    (event: React.KeyboardEvent<HTMLDivElement>) => {
      switch (event.key) {
        case 'ArrowUp':
          setValue((prevValue) => findPreviousValue(props.items, prevValue));
          break;
        case 'ArrowDown':
          setValue((prevValue) => findNextValue(props.items, prevValue));
          break;
        case 'Home':
          setValue(props.items[0]?.value ?? '');
          break;
        case 'End':
          setValue(props.items[props.items.length - 1]?.value ?? '');
          break;
        default:
          if (event.key.length === 1) {
            searchClearScheduler.cancel();
            searchRef.current += event.key.toLowerCase();
            searchClearScheduler.schedule(() => (searchRef.current = ''), 500);

            setValue((prevValue) => findSearchedValue(props.items, prevValue, searchRef.current));
          }
          break;
      }
    },
    [props.items],
  );

  useEffect(() => {
    props.onUpdate(value);
  }, [value]);

  return (
    <AriaInput>
      <StyledSelect onBlur={closeItems} onKeyDown={onKeyDown} role="listbox">
        <StyledSelectedContainer onClick={toggleItems}>
          {props.items.find((item) => item.value === value)?.label ?? ''}
          <StyledChevron tintColor={colors.white60} source="icon-chevron-down" width={22} />
        </StyledSelectedContainer>
        {itemsVisible && (
          <StyledItems>
            {props.items.map((item) => (
              <Item
                key={item.value}
                item={item}
                selected={item.value === value}
                onSelect={onSelect}
              />
            ))}
          </StyledItems>
        )}
      </StyledSelect>
    </AriaInput>
  );
}

function findPreviousValue<T extends string>(items: Array<SettingsSelectItem<T>>, currentValue: T): T {
  const currentIndex = items.findIndex((item) => item.value === currentValue) ?? 0;
  const newIndex = Math.max(currentIndex - 1, 0);
  return items[newIndex]?.value ?? '';
}

function findNextValue<T extends string>(items: Array<SettingsSelectItem<T>>, currentValue: T): T {
  const currentIndex = items.findIndex((item) => item.value === currentValue) ?? 0;
  const newIndex = Math.min(currentIndex + 1, items.length - 1);
  return items[newIndex]?.value ?? '';
}

function findSearchedValue<T extends string>(
  items: Array<SettingsSelectItem<T>>,
  currentValue: T,
  searchValue: string,
): T {
  const currentIndex = items.findIndex((item) => item.value === currentValue) ?? 0;
  const itemsFromCurrent = [...items.slice(currentIndex + 1), ...items.slice(0, currentIndex)];
  const searchedValue = itemsFromCurrent.find((item) =>
    item.label.toLowerCase().startsWith(searchValue),
  );

  return searchedValue?.value ?? currentValue;
}

const StyledItem = styled.div<{ $selected: boolean }>((props) => ({
  display: 'flex',
  alignItems: 'center',
  borderRadius: '4px',
  lineHeight: '22px',
  paddingLeft: props.$selected ? '0px' : '23px',
  paddingRight: '18px',
  '&&:hover': {
    backgroundColor: colors.blue,
  },
}));

const TickIcon = styled(ImageView)({
  marginLeft: '5px',
  marginRight: '6px',
});

interface ItemProps<T extends string> {
  item: SettingsSelectItem<T>;
  selected: boolean;
  onSelect: (key: T) => void;
}

function Item<T extends string>(props: ItemProps<T>) {
  const onClick = useCallback(() => {
    props.onSelect(props.item.value);
  }, [props.onSelect, props.item.value]);

  return (
    <StyledItem
      onClick={onClick}
      role="option"
      $selected={props.selected}
      aria-selected={props.selected}>
      {props.selected && <TickIcon tintColor={colors.white} source="icon-tick" width={12} />}
      {props.item.label}
    </StyledItem>
  );
}
