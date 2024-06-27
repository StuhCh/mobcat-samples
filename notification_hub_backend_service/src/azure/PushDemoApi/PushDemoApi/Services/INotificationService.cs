using System.Threading;
using System.Threading.Tasks;
using Microsoft.Azure.NotificationHubs;
using PushDemoApi.Models;

namespace PushDemoApi.Services
{
    public interface INotificationService
    {
        Task<bool> CreateOrUpdateInstallationAsync(DeviceInstallation deviceInstallation, CancellationToken token);
        Task<bool> DeleteInstallationByIdAsync(string installationId, CancellationToken token);
        Task<bool> RequestNotificationAsync(NotificationRequest notificationRequest, CancellationToken token);
		Task<ICollectionQueryResult<RegistrationDescription>> GetAllRegistrations(int numberOfResults);
    }
}