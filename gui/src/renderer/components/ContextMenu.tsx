import React, { useCallback, useContext, useMemo, useState } from 'react';
import styled from 'styled-components';

import { colors } from '../../config.json';
import { useStyledRef } from '../lib/utilityHooks';
import { smallText } from './common-styles';

const BORDER_WIDTH = 1;
const PADDING_VERTICAL = 10;
const ITEM_HEIGHT = 22;

type Alignment = 'left' | 'right';
type Direction = 'up' | 'down';

interface MenuContext {
  getTriggerBounds: () => DOMRect;
  visible: boolean;
}

const menuContext = React.createContext<MenuContext>({
  getTriggerBounds: () => {
    throw new Error('No trigger bounds available');
  },
  visible: false,
});

const StyledMenuContainer = styled.div({
  position: 'relative',
  padding: '8px 4px',
});

export function ContextMenuContainer(props: React.PropsWithChildren) {
  const ref = useStyledRef<HTMLDivElement>();
  const [visible, setVisible] = useState(false);

  const onClick = useCallback(() => {
    setVisible((visible) => !visible);
  }, []);

  const getTriggerBounds = useCallback(() => {
    if (ref.current === null) {
      throw new Error('No trigger bounds available');
    }
    return ref.current.getBoundingClientRect();
  }, [ref.current]);

  const contextValue = useMemo(
    () => ({
      getTriggerBounds,
      visible,
    }),
    [getTriggerBounds, visible],
  );

  return (
    <StyledMenuContainer ref={ref} onClick={onClick}>
      <menuContext.Provider value={contextValue}>{props.children}</menuContext.Provider>
    </StyledMenuContainer>
  );
}

interface StyledMenuProps {
  $direction: Direction;
  $align: Alignment;
}

const StyledMenu = styled.div<StyledMenuProps>((props) => {
  return {
    position: 'absolute',
    top: props.$direction === 'up' ? 'auto' : '100%',
    bottom: props.$direction === 'up' ? '100%' : 'auto',
    left: props.$align === 'left' ? '0' : 'auto',
    right: props.$align === 'left' ? 'auto' : '0',
    padding: '7px 4px',
    background: 'rgb(36, 53, 78)',
    border: `1px solid ${colors.darkBlue}`,
    borderRadius: '8px',
    zIndex: 1,
  };
});

const StyledMenuItem = styled.button(smallText, {
  minWidth: '110px',
  padding: '1px 10px 2px',
  lineHeight: `${ITEM_HEIGHT}px`,
  background: 'transparent',
  border: 'none',
  textAlign: 'left',

  '&&:hover': {
    background: colors.blue,
  },
});

export interface ContextMenuItem {
  label: string;
  onClick: () => void;
}

interface MenuProps {
  items: Array<ContextMenuItem>;
  align: Alignment;
}

export function ContextMenu(props: MenuProps) {
  const { getTriggerBounds, visible } = useContext(menuContext);

  const direction = useMemo<Direction>(() => {
    if (visible) {
      const bounds = getTriggerBounds();
      return bounds.y +
        bounds.height +
        props.items.length * ITEM_HEIGHT +
        2 * (BORDER_WIDTH + PADDING_VERTICAL) <
        window.innerHeight
        ? 'down'
        : 'up';
    } else {
      return 'down';
    }
  }, [visible]);

  if (!visible) {
    return null;
  }

  return (
    <StyledMenu $direction={direction} $align={props.align}>
      {props.items.map((item) => (
        <StyledMenuItem key={item.label} onClick={item.onClick}>
          {item.label}
        </StyledMenuItem>
      ))}
    </StyledMenu>
  );
}
