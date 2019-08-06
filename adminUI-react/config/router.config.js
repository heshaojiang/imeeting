export default [
    // user
    {
      path: '/user',
      component: '../layouts/UserLayout',
      routes: [
        { path: '/user', redirect: '/user/login' },
        { path: '/user/login', component: './User/Login' },
        { path: '/user/register', component: './User/Register' },
        { path: '/user/register-result', component: './User/RegisterResult' },
        { path: '/user/setting', component: './IndexPage/Setting' },
      ],
    },
    // app
    {
      path: '/',
      component: '../layouts/BasicLayout',
      Routes: ['src/pages/Authorized'],
      authority: ['role_super', 'imeeting_admin', 'imeeting_viewer', 'imeeting_participant', 'imeeting_user','imeeting_acdadmin'],
      routes: [
      { path: '/', redirect: '/account/center' },
      // admin
      {
        path: '/admin',
        name: 'admin',
        icon: 'profile',
        authority: ['role_super', 'imeeting_admin', 'imeeting_participant'],
        routes: [
          {
            path: '/admin/customer',
            name: 'customer',
            authority: ['role_super'],
            component: './Admin/Customer',
          },
          // {
          //   path: '/admin/role',
          //   name: 'role',
          //   component: './Admin/Role',
          // },
          {
            path: '/admin/user',
            name: 'user',
            authority: ['role_super', 'imeeting_admin', 'imeeting_participant'],
            component: './Admin/User',
          },
          // {
          //   path: '/admin/room',
          //   name: 'room',
          //   authority: ['role_super', 'imeeting_admin'],
          //   component: './Admin/Room',
          // },
          {
            path: '/admin/meeting',
            name: 'meetingInfo',
            authority: ['role_super', 'imeeting_admin', 'imeeting_participant'],
            component: './Admin/MeetingInfo',
          }
        ]
      },
      //acd
      {
        path: '/acd',
        name: 'acd',
        icon: 'profile',
        authority: ['role_super','imeeting_acdadmin'],
        routes:[
           {
            path: '/acd/status',
            name: 'status',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/Status',
          },
          {
            path: '/acd/queue',
            name: 'queue',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/queue/Queue',
          },
          {
            path: '/acd/agent',
            name: 'agent',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/agent/Agent',
          },
          {
            path: '/acd/calls',
            name: 'calls',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/Calls',
          },
          {
            path: '/acd/report',
            name: 'report',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/report/ReportForm'
          },
          {
            path: '/acd/evaluate',
            name: 'evaluate',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/evaluate/EvaluateRecord'
          },
           {
            path: '/acd/terminal',
            name: 'terminal',
            authority: ['role_super','imeeting_acdadmin'],
            component: './Acd/Terminal',
          },
        ]
      },
      // monitor
      // {
      //   path: '/monitor',
      //   name: 'monitor',
      //   icon: 'setting',
      //   authority: ['role_super', 'imeeting_viewer'],
      //   routes: [
      //     {
      //       path: '/monitor/service',
      //       name: 'service',
      //       component: './Monitor/Service',
      //     }
      //   ]
      // },
      // {
      //   path: '/account',
      //   routes: [
      //     {
      //       path: '/account/center',
      //       name: 'center',
      //       component: './Account/Center/Center',
      //       routes: [
      //         {
      //           path: '/account/center',
      //           redirect: '/account/center/articles',
      //         }
      //       ],
      //     },
          // {
          //   path: '/account/settings',
          //   component: './Account/Settings/Info',
          //   routes: [
          //     {
          //       path: '/account/settings',
          //       redirect: '/account/settings/base',
          //     }
          //   ],
          // },
      //   ],
      // },
      {
        name: 'exception',
        icon: 'warning',
        path: '/exception',
        hideInMenu: true,
        routes: [
          // exception
          {
            path: '/exception/403',
            name: 'not-permission',
            component: './Exception/403',
          },
          {
            path: '/exception/404',
            name: 'not-find',
            component: './Exception/404',
          },
          {
            path: '/exception/500',
            name: 'server-error',
            component: './Exception/500',
          },
          {
            path: '/exception/trigger',
            name: 'trigger',
            hideInMenu: true,
            component: './Exception/TriggerException',
          },
        ],
      },
      {
        path: '/account/center',
        component: './IndexPage/IndexPage',
      },
      {
        path: '/account/settings/base',
        component: './IndexPage/Setting',
      },
      {
        path: '/license',
        component: './IndexPage/License',
      },
      {
        component: '404',
      },
      ],
    },
  ];
  
