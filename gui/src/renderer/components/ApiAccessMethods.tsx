import { useCallback, useMemo } from 'react';

import { AccessMethodSetting } from '../../shared/daemon-rpc-types';
import { messages } from '../../shared/gettext';
import { useAppContext } from '../context';
import { useHistory } from '../lib/history';
import { generateRoutePath } from '../lib/routeHelpers';
import { RoutePath } from '../lib/routes';
import { useSelector } from '../redux/store';
import * as Cell from './cell';
import { ContextMenu, ContextMenuContainer, ContextMenuItem } from './ContextMenu';
import InfoButton from './InfoButton';
import { BackAction } from './KeyboardNavigation';
import { Layout, SettingsContainer } from './Layout';
import { NavigationBar, NavigationContainer, NavigationItems, TitleBarItem } from './NavigationBar';
import SettingsHeader, { HeaderSubTitle, HeaderTitle } from './SettingsHeader';
import { StyledContent, StyledNavigationScrollbars, StyledSettingsContent } from './SettingsStyles';
import { SmallButton, SmallButtonGroup } from './SmallButton';

export default function ApiAccessMethods() {
  const history = useHistory();
  const methods = useSelector((state) => state.settings.apiAccessMethods);

  const navigateToEdit = useCallback(
    (id?: string) => {
      const path = generateRoutePath(RoutePath.editApiAccessMethods, { id });
      history.push(path);
    },
    [history],
  );

  const navigateToNew = useCallback(() => navigateToEdit(), [navigateToEdit]);

  return (
    <BackAction action={history.pop}>
      <Layout>
        <SettingsContainer>
          <NavigationContainer>
            <NavigationBar>
              <NavigationItems>
                <TitleBarItem>
                  {
                    // TRANSLATORS: Title label in navigation bar
                    messages.pgettext('navigation-bar', 'API access methods')
                  }
                </TitleBarItem>
                <InfoButton message="TODO: Message goes here" />
              </NavigationItems>
            </NavigationBar>

            <StyledNavigationScrollbars fillContainer>
              <StyledContent>
                <SettingsHeader>
                  <HeaderTitle>
                    {messages.pgettext('navigation-bar', 'API access methods')}
                  </HeaderTitle>
                  <HeaderSubTitle>
                    {messages.pgettext(
                      'api-access-methods-view',
                      'Manage and add custom methods to access the Mullvad API.',
                    )}
                  </HeaderSubTitle>
                </SettingsHeader>

                <StyledSettingsContent>
                  <Cell.Group>
                    {methods.map((method) => (
                      <ApiAccessMethod key={method.id} method={method} />
                    ))}
                  </Cell.Group>

                  <SmallButtonGroup $noMarginTop>
                    <SmallButton onClick={navigateToNew}>
                      {messages.pgettext('api-access-methods-view', 'Add')}
                    </SmallButton>
                  </SmallButtonGroup>
                </StyledSettingsContent>
              </StyledContent>
            </StyledNavigationScrollbars>
          </NavigationContainer>
        </SettingsContainer>
      </Layout>
    </BackAction>
  );
}

interface ApiAccessMethodProps {
  method: AccessMethodSetting;
}

function ApiAccessMethod(props: ApiAccessMethodProps) {
  const { setApiAccessMethod, updateApiAccessMethod, removeApiAccessMethod } = useAppContext();
  const history = useHistory();

  const toggle = useCallback(async () => {
    const updatedMethod = cloneMethod(props.method);
    updatedMethod.enabled = !props.method.enabled;
    await updateApiAccessMethod(updatedMethod);
  }, [props.method]);

  const menuItems = useMemo<Array<ContextMenuItem>>(
    () => [
      { label: 'Use', onClick: () => setApiAccessMethod(props.method.id) },
      { label: 'Test', onClick: () => console.log('Test', props.method.name) },
      ...(props.method.type === 'direct' || props.method.type === 'bridges'
        ? []
        : [
            {
              label: 'Edit',
              onClick: () =>
                history.push(
                  generateRoutePath(RoutePath.editApiAccessMethods, { id: props.method.id }),
                ),
            },
            { label: 'Delete', onClick: () => removeApiAccessMethod(props.method.id) },
          ]),
    ],
    [props.method.id],
  );

  return (
    <Cell.Row>
      <Cell.Label>{props.method.name}</Cell.Label>
      <ContextMenuContainer>
        <Cell.Icon source="icon-close" />
        <ContextMenu items={menuItems} align="right" />
      </ContextMenuContainer>
      <Cell.Switch isOn={props.method.enabled} onChange={toggle} />
    </Cell.Row>
  );
}

function cloneMethod<T extends AccessMethodSetting>(method: T): T {
  const clonedMethod = {
    ...method,
  };

  if (
    method.type === 'socks5-remote' &&
    clonedMethod.type === 'socks5-remote' &&
    method.authentication !== undefined
  ) {
    clonedMethod.authentication = { ...method.authentication };
  }

  return clonedMethod;
}
