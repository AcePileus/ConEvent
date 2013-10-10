using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace ConEventServiceRole
{

    public class ConEventService : IConEventService
    {

        public ImageEvent GetImageEventFromId(int imageEventId)
        {
            ImageEvent imageEvent = new ImageEvent();
            
            imageEvent.EventDate = DateTime.Now;
            imageEvent.ImageCaption = "Titta vi testar!!";
            return imageEvent;
        }

        public ImageEvent[] GetAllImageEventFromDate(DateTime fromDate, bool thumbNail)
        {
            return new ImageEvent[]{};
        }

        public void SaveImageEvent(ImageEvent imageEvent)
        {
            
        }
    }
}
